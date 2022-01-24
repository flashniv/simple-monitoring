package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.common.gui.configuration;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertFilter;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/apiv1/gui/configuration/alertFilters")
public class AlertFiltersRest {

    @Autowired
    private Storage storage;

    @GetMapping("/allAlertFilters")
    @ResponseBody
    public ResponseEntity<String> getAllAlertFilters() {
        JSONArray response = new JSONArray();
        List<AlertFilter> alertFilters=storage.getAllAlertFilters();
        for (AlertFilter alertFilter:alertFilters){
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("id", alertFilter.getId());
            jsonObject.put("name", alertFilter.getName());
            jsonObject.put("expression", alertFilter.getExpression());
            response.put(jsonObject);
        }
        return ResponseEntity.ok().body(response.toString());
    }

    @PostMapping("/deleteAlertFilter")
    @ResponseBody
    public ResponseEntity<String> deleteAlertFilter(@RequestBody String payload) {
        JSONObject jsonObject=new JSONObject(payload);
        if(!jsonObject.isNull("id")){
            storage.deleteAlertFilterById(""+jsonObject.getLong("id"));
            return ResponseEntity.ok("Success");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id not found");
    }

    @PostMapping("/addAlertFilter")
    @ResponseBody
    public ResponseEntity<String> addAlertFilter(@RequestBody String payload) {
        JSONObject jsonObject=new JSONObject(payload);
        if(!jsonObject.isNull("name") && !jsonObject.isNull("expression")){
            AlertFilter alertFilter=new AlertFilter();
            alertFilter.setName(jsonObject.getString("name"));
            alertFilter.setExpression(jsonObject.getString("expression"));
            storage.saveAlertFilter(alertFilter);

            return ResponseEntity.ok("Success");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id not found");
    }
}
