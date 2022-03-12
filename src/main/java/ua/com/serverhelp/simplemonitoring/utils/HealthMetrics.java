package ua.com.serverhelp.simplemonitoring.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemonitoring.alerter.Alerter;
import ua.com.serverhelp.simplemonitoring.entities.alerts.Alert;
import ua.com.serverhelp.simplemonitoring.entities.trigger.Trigger;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;
import ua.com.serverhelp.simplemonitoring.queue.QueueElement;

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
            metricsQueue.putData(new QueueElement("internal.cron.runCheckMetrics", "{}", Instant.now(), 1.0));
        }else{
            metricsQueue.putData(new QueueElement("internal.cron.runCheckMetrics", "{}", Instant.now(), 0.0));
            Trigger trigger=new Trigger(){
                @Override
                public String getName() {
                    return "Check metrics not running";
                }

                @Override
                public String getDescription() {
                    return "Check metrics not running";
                }
            };
            trigger.setHost("internal.cron.runCheckMetrics");
            Alert alert=new Alert();
            alert.setStartDate(Instant.now());
            alert.setTrigger(trigger);
            alerter.printAlert(alert);
        }
        if(checkMetricsCommit()){
            metricsQueue.putData(new QueueElement("internal.cron.checkMetricsCommit", "{}", Instant.now(), 1.0));
        }else{
            metricsQueue.putData(new QueueElement("internal.cron.checkMetricsCommit", "{}", Instant.now(), 0.0));
            Trigger trigger=new Trigger(){
                @Override
                public String getName() {
                    return "Metrics commit not running";
                }

                @Override
                public String getDescription() {
                    return "Metrics commit not running";
                }
            };
            trigger.setHost("internal.cron.checkMetricsCommit");
            Alert alert=new Alert();
            alert.setStartDate(Instant.now());
            alert.setTrigger(trigger);
            alerter.printAlert(alert);
        }
    }
}
