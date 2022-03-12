package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;
import ua.com.serverhelp.simplemonitoring.queue.QueueElement;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.MYLog;

import java.time.Instant;

@RestController
@RequestMapping("/apiv1/metric/dailyboolean")
public class DailyBooleanMetricRest {
    @Autowired
    private Storage storage;
    @Autowired
    private MetricsQueue metricsQueue;

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<String> getAddEvent(@RequestParam String path, @RequestParam(defaultValue = "true") Boolean value){
        double val=0;
        if(value){
            val=1;
        }

        metricsQueue.putData(new QueueElement(path, "{}",Instant.now(), val));
        storage.createIfNotExistTrigger(path,"{}","ua.com.serverhelp.simplemonitoring.entities.trigger.BooleanChecker");
        storage.createIfNotExistTrigger(path,"{}","ua.com.serverhelp.simplemonitoring.entities.trigger.Last24hValuesChecker");

        MYLog.printDebug1("DailyBooleanMetricRest::getAddEvent /apiv1/metric/dailyboolean Event add:"+value);
        return ResponseEntity.ok().body("Success");
    }
}
