package ua.com.serverhelp.simplemonitoring.service.alert;

import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alert;
import ua.com.serverhelp.simplemonitoring.service.alert.alerters.AlertSender;
import ua.com.serverhelp.simplemonitoring.service.alert.alerters.DummyAlertSender;

@Service
public class AlertService {
    public void sendAlert(Alert alert) {
        AlertSender alertSender = new DummyAlertSender();
        alertSender.sendMessage(alert);
    }
}
