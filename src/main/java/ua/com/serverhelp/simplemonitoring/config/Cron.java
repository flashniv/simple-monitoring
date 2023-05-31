package ua.com.serverhelp.simplemonitoring.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ua.com.serverhelp.simplemonitoring.service.DataItemsService;
import ua.com.serverhelp.simplemonitoring.service.TriggerService;

@Configuration
@EnableScheduling
@Slf4j
public class Cron {
    @Autowired
    private DataItemsService dataItemsService;
    @Autowired
    private TriggerService triggerService;

    @Scheduled(fixedDelay = 30000, initialDelay = 90000)
    public void processDataItemQueue() {
        log.debug("Cron::processDataItemQueue start");
        try {
            dataItemsService.processItems();
        } catch (Exception e) {
            log.error("Error metric writing", e);
        }
        log.debug("Cron::processDataItemQueue done");
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 90000)
    public void checkTriggers() {
        log.debug("Cron::checkTriggers start");

        triggerService.cronCheckTriggers();

        log.debug("Cron::checkTriggers done");
    }
}
