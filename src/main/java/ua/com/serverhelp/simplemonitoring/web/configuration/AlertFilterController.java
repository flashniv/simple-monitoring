package ua.com.serverhelp.simplemonitoring.web.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertFilter;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.util.List;

@Controller
@RequestMapping("/configuration/alertFilters")
public class AlertFilterController {
    @Autowired
    Storage storage;

    @GetMapping("")
    public String allAlertFilters(Model model) {
        List<AlertFilter> alertFilters=storage.getAllAlertFilters();
        model.addAttribute("alertFilters", alertFilters);
        return "alert_filters";
    }
    @GetMapping("/add")
    public String addAlertFilters(Model model) {
        model.addAttribute("alertFilter", new AlertFilter());
        return "alert_filter_add";
    }

    @PostMapping("/add")
    public String addPOSTAlertFilters(@ModelAttribute AlertFilter alertFilter) {
        storage.saveAlertFilter(alertFilter);
        return "redirect:/configuration/alertFilters";
    }
    @PostMapping("/delete")
    public String deleteAlertFilters(@RequestParam String delete) {
        storage.deleteAlertFilterById(delete);
        return "redirect:/configuration/alertFilters";
    }
}
