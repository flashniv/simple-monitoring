package ua.com.serverhelp.simplemonitoring.web.root;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.serverhelp.simplemonitoring.entities.event.Event;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.IParameterGroup;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.*;

@Controller
@RequestMapping(path = "/metrics")
public class MetricsViewController {
    @Autowired
    private Storage storage;

    @GetMapping("")
    public String allMetrics(
            @RequestParam(required = false, defaultValue = "") String filter,
            @RequestParam(required = false) boolean onlyalerted,
            Model model
    ) {
        List<Metric> metrics = storage.getAllMetrics();
        List<HashMap<String, Object>> list = new ArrayList<>();
        //Metric foreach
        for (Metric metric : metrics) {
            if (!metric.getPath().matches(".*" + filter + ".*")) continue;
            HashMap<String, Object> metricMap = new HashMap<>();
            metricMap.put("path", metric.getPath());
            list.add(metricMap);
        }
        model.addAttribute("metrics", list);
        model.addAttribute("onlyalerted",onlyalerted);
        model.addAttribute("filter",filter);

        return "metrics";
    }

    @GetMapping("/detail")
    public String events(
            @RequestParam(defaultValue = "") String path,
            @RequestParam(required = false, defaultValue = "-60") Integer beginPeriod,
            @RequestParam(required = false, defaultValue = "0") Integer endPeriod,
            Model model
    ) {
        Metric metric = storage.getMetric(path);
        List<IParameterGroup> parameterGroups=storage.getAllTypeParameterGroups(metric);
        List<HashMap<String,Object>> table=new ArrayList<>();

        for (IParameterGroup parameterGroup:parameterGroups){
            HashMap<String,Object> tableRow=new HashMap<>();
            tableRow.put("parameterGroup",parameterGroup);

            List<Event> events=storage.getEventsByParameterGroup(parameterGroup, Instant.now().plus(beginPeriod, ChronoUnit.MINUTES), Instant.now().plus(endPeriod, ChronoUnit.MINUTES));
            HashMap<String,Double> limits=getLimits(events);

            tableRow.put("limits",limits);
            table.add(tableRow);
        }

        model.addAttribute("metric", metric);
        model.addAttribute("table", table);
        model.addAttribute("beginPeriod",beginPeriod);
        model.addAttribute("endPeriod",endPeriod);
        return "metrics_detail";
    }

    private HashMap<String, Double> getLimits(List<Event> events) {
        HashMap<String,Double> res=new HashMap<>();
        if(!events.isEmpty()) {
            Double max = events.get(0).getValue();
            Double min = events.get(0).getValue();
            Double sum = 0.0;
            for (Event event : events) {
                if (event.getValue() < min) {
                    min = event.getValue();
                }
                if (event.getValue() > max) {
                    max = event.getValue();
                }
                sum += event.getValue();
            }
            res.put("min", min);
            res.put("max", max);
            res.put("avg", sum / events.size());
            res.put("last", events.get(events.size() - 1).getValue());
        }else{
            res.put("min", Double.NaN);
            res.put("max", Double.NaN);
            res.put("avg", Double.NaN);
            res.put("last", Double.NaN);
        }
        return res;
    }

    @GetMapping(path = "/detail/chart/{width}/{height}/{precision}")
    public void buildChart(
            @RequestParam String path,
            @RequestParam(required = false, defaultValue = "-60") Integer beginPeriod,
            @RequestParam(required = false, defaultValue = "0") Integer endPeriod,
            @PathVariable("width") int width,
            @PathVariable("height") int height,
            @PathVariable("precision") int precision,
            HttpServletResponse response
    ) throws IOException {
        Metric metric = storage.getMetric(path);
        if (metric != null) {
            Paint[] paints={
                    new Color(0,0,0),
                    new Color(255,0,0),
                    new Color(0, 255, 0),
                    new Color(0, 0, 255),
                    new Color(255,0,255),
                    new Color(255,100,0),
                    new Color(0, 120, 0),
                    new Color(255, 205, 0),
                    new Color(170,0,0),
                    new Color(21, 128, 120),
                    new Color(153, 0, 102)
            };
            // Create dataset
            XYDataset dataset = getDataSet(metric,beginPeriod,endPeriod,precision);
            // Create chart
            JFreeChart chart = ChartFactory.createTimeSeriesChart(
                    "", // Chart
                    "", // X-Axis Label
                    "", // Y-Axis Label
                    dataset,
                    true,
                    false,
                    false
            );

            XYPlot plot = chart.getXYPlot();
            //set metric line colors
            for (int i = 0; i < dataset.getSeriesCount(); i++) {
                if(i<paints.length) {
                    plot.getRenderer().setSeriesPaint(i, paints[i]);
                }
            }
            //Set line width
            plot.getRenderer().setBaseStroke(new BasicStroke(2.0f));
            ((AbstractXYItemRenderer) plot.getRenderer()).setAutoPopulateSeriesStroke(false);
            //Changes background color
            plot.setBackgroundPaint(new Color(255, 255, 255));
            //Set grid
            plot.setRangeGridlinesVisible(true);
            plot.setRangeGridlinePaint(Color.BLACK);
            plot.setDomainGridlinesVisible(true);
            plot.setDomainGridlinePaint(Color.BLACK);

            writeChartAsPNGImage(chart, width, height, response);
        }
    }

    private XYDataset getDataSet(Metric metric, Integer beginPeriod, Integer endPeriod, int precision) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        List<IParameterGroup> parameterGroups=storage.getAllTypeParameterGroups(metric);

        for (IParameterGroup parameterGroup:parameterGroups){
            List<Event> events=storage.getEventsByParameterGroup(parameterGroup, Instant.now().plus(beginPeriod, ChronoUnit.MINUTES), Instant.now().plus(endPeriod, ChronoUnit.MINUTES),true);
            StringBuilder seriesName= new StringBuilder();
            for(Map.Entry<String,String> entry: parameterGroup.getParameters().entrySet()){
                seriesName.append(entry.getKey()).append(":").append(entry.getValue()).append(" ");
            }
            TimeSeries timeSeries=new TimeSeries(seriesName);

            for (Event event : events) {
                RegularTimePeriod regularTimePeriod = new Minute(Date.from(event.getTimestamp()));
                timeSeries.addOrUpdate(regularTimePeriod, event.getValue()); //TODO release precision
            }
            dataset.addSeries(timeSeries);
        }
        return dataset;
    }

    private void writeChartAsPNGImage(final JFreeChart chart, final int width, final int height, HttpServletResponse response) throws IOException {
        final BufferedImage bufferedImage = chart.createBufferedImage(width, height);

        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        ChartUtilities.writeBufferedImageAsPNG(response.getOutputStream(), bufferedImage);
    }

    private int getPrecision(double num) {
        int res = 0;
        while (num > 10000) {
            num /= 1000;
            res++;
        }
        return res;
    }
}
