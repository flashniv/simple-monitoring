package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.common.gui;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entities.alerts.Alert;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertFilters;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/apiv1/gui/history")
public class HistoryRest {

    @Autowired
    private Storage storage;
    @Autowired
    private MetricsQueue metricsQueue;
    @Autowired
    private AlertFilters alertFilters;

    @GetMapping("/allProblems")
    @ResponseBody
    public ResponseEntity<String> getAllProblems() {
        List<Alert> alertList= storage.getAllAlerts(Sort.by(Sort.Direction.DESC,"startDate"));
        JSONArray response=new JSONArray();
        for (Alert alert:alertList){
            JSONObject jsonAlert=new JSONObject();
            jsonAlert.put("id", alert.getId());
            jsonAlert.put("startDate", alert.getStartDate());
            jsonAlert.put("stopDate", alert.getStopDate());
            jsonAlert.put("operationData", alert.getOperationData());
            jsonAlert.put("host", alert.getTrigger().getHost());
            jsonAlert.put("triggerName", alert.getTrigger().getName());
            jsonAlert.put("triggerDescription", alert.getTrigger().getDescription());
            jsonAlert.put("isFiltered", alertFilters.matchFilters(alert));
            response.put(jsonAlert);
        }
        return ResponseEntity.ok().body(response.toString());
    }
}
