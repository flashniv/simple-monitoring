package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.common.gui;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entities.alerts.Alert;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/apiv1/gui/dashboard")
public class DashboardRest {

    @Autowired
    private Storage storage;
    @Autowired
    private MetricsQueue metricsQueue;

    @GetMapping("/currentProblems")
    @ResponseBody
    public ResponseEntity<String> getCurrentProblems() {
        List<Alert> alertList= storage.getAlerts(Instant.now().minus(2, ChronoUnit.DAYS), Instant.now(), Sort.by(Sort.Direction.DESC,"startDate"));
        JSONArray response=new JSONArray();
        for (Alert alert:alertList){
            if (alert.getStopDate()!=null) continue;
            JSONObject jsonAlert=new JSONObject();
            jsonAlert.put("id", alert.getId());
            jsonAlert.put("startDate", alert.getStartDate());
            jsonAlert.put("operationData", alert.getOperationData());
            jsonAlert.put("host", alert.getTrigger().getHost());
            jsonAlert.put("triggerName", alert.getTrigger().getName());
            jsonAlert.put("triggerDescription", alert.getTrigger().getDescription());
            response.put(jsonAlert);
        }
        return ResponseEntity.ok().body(response.toString());
    }
}
