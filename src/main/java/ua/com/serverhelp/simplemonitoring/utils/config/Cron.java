package ua.com.serverhelp.simplemonitoring.utils.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemonitoring.alerter.Alerter;
import ua.com.serverhelp.simplemonitoring.entities.trigger.Triggers;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.HealthMetrics;
import ua.com.serverhelp.simplemonitoring.utils.MYLog;

@Component
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
        triggers.checkTriggers();
        healthMetrics.updateCheckTriggers();
    }

    @Scheduled(initialDelay = 300000L,fixedRate = 7200000L)
    public void clearHistory(){
        MYLog.printInfo("Cron::clearHistory Start clear");
        storage.clearHistory();
        MYLog.printInfo("Cron::clearHistory Stop clear");
    }

    @Scheduled(initialDelay = 60000L,fixedRate = 90000L)
    public void commitHistory(){
        MYLog.printInfo("Cron::clearHistory Start commit history");
        storage.commitHistory();
        healthMetrics.updateMetricCommit();
        MYLog.printInfo("Cron::clearHistory Stop commit history");
    }

    @Scheduled(initialDelay = 300000L,fixedRate = 300000L)
    public void checkHealth(){
        MYLog.printInfo("Cron::clearHistory Start check health");
        healthMetrics.commitHealthMetric();
        MYLog.printInfo("Cron::clearHistory Stop check health");
    }

}
