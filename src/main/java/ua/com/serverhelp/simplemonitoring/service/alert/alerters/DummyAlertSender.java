package ua.com.serverhelp.simplemonitoring.service.alert.alerters;

import lombok.extern.slf4j.Slf4j;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alert;

@Slf4j
public class DummyAlertSender implements AlertSender {
    @Override
    public void initialize(String jsonParams) {

    }

    @Override
    public void sendMessage(Alert alert) {
        String errorText = alert.getTriggerStatus().name() + ": " +
                alert.getTrigger().getName() + "\n" +
                "time " + alert.getAlertTimestamp();
        log.debug(errorText);
    }
}
