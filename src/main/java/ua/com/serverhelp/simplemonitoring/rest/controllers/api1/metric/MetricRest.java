package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.util.List;

@RestController
@RequestMapping("/apiv1/metric")
public class MetricRest {
    @Autowired
    private Storage storage;

    @GetMapping("/")
    @ResponseBody
    public List<Metric> getMetrics(){
        return storage.getAllMetrics();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Metric getMetricsByID(@PathVariable String id){
        return storage.getMetric(id);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteMetricByID(@PathVariable String id){
        if(storage.deleteMetric(id)){
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.ok().body("Failure");
    }
}
