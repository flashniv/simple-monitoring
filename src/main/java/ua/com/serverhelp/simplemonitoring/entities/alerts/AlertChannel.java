package ua.com.serverhelp.simplemonitoring.entities.alerts;

import io.sentry.Sentry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.json.JSONObject;
import ua.com.serverhelp.simplemonitoring.alerter.AlertSender;
import ua.com.serverhelp.simplemonitoring.alerter.SimpleTelegramBot;
import ua.com.serverhelp.simplemonitoring.entities.account.User;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import javax.persistence.*;
import java.io.IOException;
import java.util.List;

@Entity
@Data
@Slf4j
public class AlertChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne (optional=false)
    @JoinColumn (name="owner_id")
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private User owner;
    private String alerterClass;
    private String parameters;

    public void printAlert(Storage storage,Alert alert){
        List<AlertChannelFilter> alertChannelFilters=storage.getAlertChannelFilters(this);
        boolean matched=false;
        for (AlertChannelFilter alertChannelFilter: alertChannelFilters){
            if(alertChannelFilter.matchFilter(alert)){
                matched=true;
            }
        }
        if(matched) {
            AlertSender alertSender=null;
            if (alerterClass.equals("ua.com.serverhelp.simplemonitoring.alerter.SimpleTelegramBot")) {
                alertSender = new SimpleTelegramBot(new JSONObject(parameters));
            }
            if (alertSender!=null){
                try {
                    if (alert.getStopDate() == null) {
                        alertSender.sendMessage("<b>ERR " + alert.getTrigger().getName() + " in path " + alert.getTrigger().getHost() + "</b>\non event time " + alert.getStartDate());
                    } else {
                        alertSender.sendMessage("<b>OK " + alert.getTrigger().getName() + " in path " + alert.getTrigger().getHost() + "</b>\non event time " + alert.getStopDate());
                    }
                }catch (IOException exception){
                    Sentry.captureException(exception);
                    log.error("Error send alert message",exception);
                }
            }
        }
    }
}
