package ua.com.serverhelp.simplemonitoring.service.alert.alerters;

import com.fasterxml.jackson.core.JsonProcessingException;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alert;

public interface AlertSender {
    void initialize(String jsonParams) throws JsonProcessingException;

    void sendMessage(Alert alert) throws Exception;
}
