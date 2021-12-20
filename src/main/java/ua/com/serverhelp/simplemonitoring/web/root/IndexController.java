package ua.com.serverhelp.simplemonitoring.web.root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ua.com.serverhelp.simplemonitoring.entities.alerts.Alert;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.HealthMetrics;
import ua.com.serverhelp.simplemonitoring.utils.StringFormat;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private Storage storage;
    @Autowired
    private HealthMetrics healthMetrics;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/index/getCurrentProblems")
    @ResponseBody
    public ResponseEntity<String> getCurrentProblems() {
        List<Alert> alertList= storage.getAlerts(Instant.now().minus(2, ChronoUnit.DAYS), Instant.now(), Sort.by(Sort.Direction.DESC,"startDate"));
        StringBuilder table=new StringBuilder();
        table.append("<tr>");
        table.append("<th>Age</th>");
        table.append("<th>Path</th>");
        table.append("<th>Alert</th>");
        table.append("</tr>");
        for(Alert alert:alertList){
            if (alert.getStopDate()!=null) continue;
            table.append("<tr>");
            table.append("<td>").append(StringFormat.getTimeAgo(alert.getStartDate())).append("</td>");
            table.append("<td>").append(alert.getTrigger().getHost()).append("</td>"); //TODO release it
            table.append("<td>").append(alert.getTrigger().getDescription()).append("</td>");
            table.append("</tr>");
        }

        return ResponseEntity.ok().body(table.toString());
    }
    @GetMapping("/index/status")
    @ResponseBody
    public ResponseEntity<String> getStatus() {
        StringBuilder table=new StringBuilder();

        //Check metrics
        table.append("<tr>");
        table.append("<td>").append("Check metrics").append("</td>");
        if(healthMetrics.checkRunCheckMetrics()){
            table.append("<td class=\"bg-success text-white\">").append("OK (").append(Duration.between(healthMetrics.getRunCheckMetrics(), Instant.now()).getSeconds()).append(" sec ago)").append("</td>");
        }else{
            table.append("<td class=\"bg-danger text-white\">").append("Fail").append("</td>");
        }
        table.append("</tr>");

        //Commit metrics
        table.append("<tr>");
        table.append("<td>").append("Commit collectd metrics").append("</td>");
        if(healthMetrics.checkMetricsCommit()){
            table.append("<td class=\"bg-success text-white\">").append("OK (").append(Duration.between(healthMetrics.getMetricCommit(), Instant.now()).getSeconds()).append(" sec ago)").append("</td>");
        }else{
            table.append("<td class=\"bg-danger text-white\">").append("Fail").append("</td>");
        }
        table.append("</tr>");

        //Events per sec
        table.append("<tr>");
        table.append("<td>").append("Events/s").append("</td>");
        table.append("<td>").append(String.format("%,.3f",healthMetrics.getEventPerSecond())).append("</td>");
        table.append("</tr>");

        return ResponseEntity.ok().body(table.toString());
    }

}
