package ua.com.serverhelp.simplemonitoring.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ua.com.serverhelp.simplemonitoring.service.DataItemsService;

@Configuration
@EnableScheduling
@Slf4j
public class Cron {
    @Autowired
    private DataItemsService dataItemsService;

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
}
