package ua.com.serverhelp.simplemonitoring.web.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/configuration/metrics")
public class MetricsController {
    @Autowired
    Storage storage;

    @GetMapping("")
    public String allMetrics(Model model){
        List<Metric> metrics=storage.getAllMetrics();
        /*List<Alert> alerts=storage.getAllOpenedAlerts(); //TODO release it

        List<HashMap<String,Object>> list=new ArrayList<>();
        //Metric foreach
        for(Metric metric:metrics){
            boolean alerted=false;

            HashMap<String,Object> metricMap=new HashMap<>();
            //Check alerts
            for(Alert alert:alerts){
                if(alert.getStopProblem()!=null) continue;
                if (alert.getMetric().getPath().equals(metric.getPath())) {
                    alerted = true;
                    break;
                }
            }
            metricMap.put("path", metric.getPath());
            metricMap.put("alerted", alerted);
            list.add(metricMap);
        }*/
        List<HashMap<String,Object>> list=new ArrayList<>();
        //Metric foreach
        for(Metric metric:metrics){
            HashMap<String,Object> metricMap=new HashMap<>();
            metricMap.put("path", metric.getPath());
            metricMap.put("alerted", false);
            list.add(metricMap);
        }
        model.addAttribute("metrics", list);
        return "configuration_metrics";
    }

    @PostMapping("")
    public String deleteMetricByPOST(@RequestParam String delete,@RequestParam List<String> path,Model model) {
        int deleteCount=0;
        for (String onePath : path){
            if(storage.deleteMetric(onePath)){
                deleteCount++;
            }
        }
        model.addAttribute("deleteCount",deleteCount);
        return allMetrics(model);
    }
}
