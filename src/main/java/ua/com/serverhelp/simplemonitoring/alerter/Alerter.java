package ua.com.serverhelp.simplemonitoring.alerter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemonitoring.entities.alerts.Alert;
import ua.com.serverhelp.simplemonitoring.entities.alerts.AlertChannel;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.util.List;

@Component
public class Alerter {
    @Autowired
    private Storage storage;

    public void printAlert(Alert alert) {
        List<AlertChannel> alertChannels=storage.getAllAlertChannels();
        for (AlertChannel alertChannel:alertChannels){
            alertChannel.printAlert(alert);
        }
    }
}
