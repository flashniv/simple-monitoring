package ua.com.serverhelp.simplemonitoring.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ua.com.serverhelp.simplemonitoring.service.DataItemsService;
import ua.com.serverhelp.simplemonitoring.service.TriggerService;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.FileManagementService;

import java.time.Duration;
import java.time.Instant;

@Configuration
@EnableScheduling
@Slf4j
public class Cron {
    @Autowired
    private DataItemsService dataItemsService;
    @Autowired
    private TriggerService triggerService;
    @Autowired
    private FileManagementService fileManagementService;

    @Value("${application.cron.runCheckTriggers:true}")
    private boolean runCheckTriggers;

    @Scheduled(fixedDelay = 30000, initialDelay = 90000)
    public void processDataItemQueue() {
        Instant start = Instant.now();
        log.debug("Cron::processDataItemQueue start");
        try {
            dataItemsService.processItems();
        } catch (Exception e) {
            log.error("Error metric writing", e);
        }
        log.debug("Cron::processDataItemQueue done " + Duration.between(start, Instant.now()).getSeconds());
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 90000)
    public void checkTriggers() {
        if (runCheckTriggers) {
            Instant start = Instant.now();
            log.debug("Cron::checkTriggers start");

            triggerService.cronCheckTriggers();

            log.debug("Cron::checkTriggers done " + Duration.between(start, Instant.now()).getSeconds());
        }
    }

    @Scheduled(fixedDelay = 80000000L, initialDelay = 120000)
    public void clearMetricDirectory() {
        Instant start = Instant.now();
        log.info("Cron::clearMetricDirectory start");

        fileManagementService.clearMetricDir();

        log.info("Cron::clearMetricDirectory done " + Duration.between(start, Instant.now()).getSeconds());
    }
}
