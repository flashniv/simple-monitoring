package ua.com.serverhelp.simplemonitoring.service.alert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alert;
import ua.com.serverhelp.simplemonitoring.repository.AlerterRepository;
import ua.com.serverhelp.simplemonitoring.service.alert.alerters.AlertSender;

import java.lang.reflect.InvocationTargetException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {
    private final AlerterRepository alerterRepository;

    public void sendAlert(Alert alert) {
        alerterRepository.findAllByOrganization(alert.getOrganization()).stream()
                .filter(alerter -> alert.getTrigger().getPriority().getValue() >= alerter.getMinPriority().getValue())
                .forEach(alerter -> {
                    try {
                        String className = alerter.getClassName();
                        Class<?> classType = Class.forName(className);

                        AlertSender alertSender = (AlertSender) classType.getConstructor().newInstance();
                        alertSender.initialize(alerter.getProperties());

                        alertSender.sendMessage(alert);
                    } catch (ClassNotFoundException | InvocationTargetException | InstantiationException |
                             IllegalAccessException | NoSuchMethodException e) {
                        log.error("Error sending alert over " + alerter.getClassName() + " with params " + alerter.getProperties(), e);
                    }
                });
    }
}
