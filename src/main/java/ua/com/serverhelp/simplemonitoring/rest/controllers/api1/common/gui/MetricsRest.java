package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.common.gui;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entities.event.Event;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.IParameterGroup;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/apiv1/gui/metrics")
public class MetricsRest {

    @Autowired
    private Storage storage;
    @Autowired
    private MetricsQueue metricsQueue;

    @GetMapping("/allMetrics")
    @ResponseBody
    public ResponseEntity<String> getAllMetrics() {
        JSONArray resp=new JSONArray(storage.getAllMetrics().toArray());
        return ResponseEntity.ok().body(resp.toString());
    }

    @GetMapping("/events")
    public ResponseEntity<String> events(
            @RequestParam(defaultValue = "") String path,
            @RequestParam(required = false, defaultValue = "-60") Integer beginPeriod,
            @RequestParam(required = false, defaultValue = "0") Integer endPeriod,
            Model model
    ) {
        JSONArray res=new JSONArray();

        Metric metric = storage.getMetric(path);
        List<IParameterGroup> parameterGroups=storage.getAllTypeParameterGroups(metric);
        
        for (IParameterGroup parameterGroup:parameterGroups){
            List<Event> events=storage.getEventsByParameterGroup(parameterGroup, Instant.now().plus(beginPeriod, ChronoUnit.MINUTES), Instant.now().plus(endPeriod, ChronoUnit.MINUTES),true);
            for (int i = 0; i < events.size(); i++) {
                JSONObject event=null;
                if(res.length()-1>i){
                    event= res.getJSONObject(i);
                }
                if(event==null){
                    event=new JSONObject();
                }
                event.put("time", events.get(i).getTimestamp());
                event.put(parameterGroup.getJson(), events.get(i).getValue());
                res.put(i, event);
            }
        }

        return ResponseEntity.ok(res.toString());
    }

    @GetMapping("/limits")
    public ResponseEntity<String> limits(
            @RequestParam(defaultValue = "") String path,
            @RequestParam(required = false, defaultValue = "-60") Integer beginPeriod,
            @RequestParam(required = false, defaultValue = "0") Integer endPeriod,
            Model model
    ) {
        JSONArray res=new JSONArray();

        Metric metric = storage.getMetric(path);
        List<IParameterGroup> parameterGroups=storage.getAllTypeParameterGroups(metric);

        for (IParameterGroup parameterGroup:parameterGroups){
            List<Event> events=storage.getEventsByParameterGroup(parameterGroup, Instant.now().plus(beginPeriod, ChronoUnit.MINUTES), Instant.now().plus(endPeriod, ChronoUnit.MINUTES));
            JSONObject limits=getLimits(events);
            limits.put("name", parameterGroup.getJson());

            res.put(limits);
        }
        return ResponseEntity.ok().body(res.toString());
    }

    private JSONObject getLimits(List<Event> events) {
        JSONObject res=new JSONObject();
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
//            res.put("min", Double.NaN);
//            res.put("max", Double.NaN);
//            res.put("avg", Double.NaN);
//            res.put("last", Double.NaN);
        }
        return res;
    }

    //TODO delete it
    /*@GetMapping(path = "/chart/{width}/{height}/{precision}")
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
*/
}
