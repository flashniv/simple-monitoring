package ua.com.serverhelp.simplemonitoring.service.alert.alerters;

import ua.com.serverhelp.simplemonitoring.entity.alert.Alert;

public interface AlertSender {
    void initialize(String jsonParams);

    void sendMessage(Alert alert);
}
