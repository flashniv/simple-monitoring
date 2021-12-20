package ua.com.serverhelp.simplemonitoring.entities.alerts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.util.List;

@Component
public class AlertFilters {
    @Autowired
    Storage storage;

    public boolean matchFilters(Alert alert){
        List<AlertFilter> alertFilters=storage.getAllAlertFilters();
        for (AlertFilter alertFilter:alertFilters){
            if(alertFilter.matchFilter(alert)){
                return true;
            }
        }
        return false;
    }
}
