package ua.com.serverhelp.simplemonitoring.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemonitoring.alerter.Alerter;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.time.Duration;
import java.time.Instant;

@Data
@Component
public class HealthMetrics {
    @Autowired
    private MetricsQueue metricsQueue;
    @Autowired
    private Alerter alerter;

    private Instant runCheckMetrics;
    private Instant metricCommit;
    private long eventCount=0;
    private final Instant eventUpdate= Instant.now();

    public void incEventCount(){
        eventCount++;
    }

    public double getEventPerSecond(){
        return (double) eventCount/ Duration.between(eventUpdate, Instant.now()).getSeconds();
    }

    public void updateCheckTriggers(){
        runCheckMetrics=Instant.now();
    }

    public void updateMetricCommit(){
        metricCommit=Instant.now();
    }

    public boolean checkRunCheckMetrics(){
        if(runCheckMetrics==null){
            return false;
        } else {
            return Duration.between(runCheckMetrics, Instant.now()).getSeconds() <= 300;
        }
    }
    public boolean checkMetricsCommit(){
        if(metricCommit==null){
            return false;
        } else {
            return Duration.between(metricCommit, Instant.now()).getSeconds() <= 300;
        }
    }

    public void commitHealthMetric(){
        if(checkRunCheckMetrics()){
            metricsQueue.putData("internal.cron.runCheckMetrics", "{}", "{}", Instant.now(), 1.0);
        }else{
            metricsQueue.putData("internal.cron.runCheckMetrics", "{}", "{}", Instant.now(), 0.0);
            alerter.printAlert("Check metrics not running");
        }
        if(checkMetricsCommit()){
            metricsQueue.putData("internal.cron.checkMetricsCommit", "{}", "{}", Instant.now(), 1.0);
        }else{
            metricsQueue.putData("internal.cron.checkMetricsCommit", "{}", "{}", Instant.now(), 0.0);
            alerter.printAlert("Metrics commit not running");
        }
    }
}
