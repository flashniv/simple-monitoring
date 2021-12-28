package ua.com.serverhelp.simplemonitoring.utils.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemonitoring.alerter.Alerter;
import ua.com.serverhelp.simplemonitoring.entities.trigger.Triggers;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.HealthMetrics;

@Component
@Slf4j
public class Cron {
    @Autowired
    private Storage storage;
    @Autowired
    private Alerter alerter;
    @Autowired
    private Triggers triggers;
    @Autowired
    private HealthMetrics healthMetrics;

    @Scheduled(initialDelay = 120000L,fixedRate = 120000L)
    public void checkTriggers(){
        log.info("Cron::checkTriggers start");
        triggers.checkTriggers();
        healthMetrics.updateCheckTriggers();
        log.info("Cron::checkTriggers complete");
    }

    @Scheduled(initialDelay = 300000L,fixedRate = 7200000L)
    public void clearHistory(){
        log.info("Cron::clearHistory Start clear");
        storage.clearHistory();
        log.info("Cron::clearHistory Stop clear");
    }

    @Scheduled(initialDelay = 60000L,fixedRate = 90000L)
    public void commitHistory(){
        log.info("Cron::clearHistory Start commit history");
        storage.commitHistory();
        healthMetrics.updateMetricCommit();
        log.info("Cron::clearHistory Stop commit history");
    }

    @Scheduled(initialDelay = 300000L,fixedRate = 300000L)
    public void checkHealth(){
        log.info("Cron::clearHistory Start check health");
        healthMetrics.commitHealthMetric();
        log.info("Cron::clearHistory Stop check health");
    }

}
