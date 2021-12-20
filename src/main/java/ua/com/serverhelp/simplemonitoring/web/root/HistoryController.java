package ua.com.serverhelp.simplemonitoring.web.root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ua.com.serverhelp.simplemonitoring.entities.alerts.Alert;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(path = "/history")
public class HistoryController {
    @Autowired
    Storage storage;

    @GetMapping("")
    public String history(
            @RequestParam(required = false, defaultValue = "") String filter,
            @RequestParam(required = false, defaultValue = "true") boolean onlyalerted, //TODO add save filters to DB
            Model model
    ) {
        List<Alert> alerts=storage.getAlerts(Instant.now().minus(7,ChronoUnit.DAYS),Instant.now(), Sort.by(Sort.Direction.DESC,"startDate"));
        List<Alert> res=new ArrayList<>();
        for (Alert alert:alerts){
            if(alert.getStopDate()!=null && onlyalerted) continue;
            if(!alert.getTrigger().getHost().matches(".*" + filter + ".*")) continue;
            res.add(alert);
        }
        model.addAttribute("alerts",res);
        model.addAttribute("onlyalerted",onlyalerted);
        model.addAttribute("filter",filter);

        return "alerts";
    }
}
