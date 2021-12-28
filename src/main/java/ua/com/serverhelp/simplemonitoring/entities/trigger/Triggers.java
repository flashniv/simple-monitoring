package ua.com.serverhelp.simplemonitoring.entities.trigger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemonitoring.alerter.Alerter;
import ua.com.serverhelp.simplemonitoring.entities.alerts.Alert;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.CheckTriggerException;
import ua.com.serverhelp.simplemonitoring.utils.MYLog;
import ua.com.serverhelp.simplemonitoring.utils.MetricUnreachableException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class Triggers {
    @Autowired
    private Storage storage;
    @Autowired
    private Alerter alerter;

    public void checkTriggers(){
        List<Trigger> triggers = storage.getAllTriggers();
        for(Trigger trigger: triggers){
            try {
                boolean triggerState=trigger.checkState(storage);
                if(!triggerState){
                    log.debug("Triggers::checkTriggers trigger false "+trigger.getHost()+" name "+trigger.getName());
                }
                Optional<Alert> optionalAlert=storage.getFirstAlertByTriggerAndStopDateIsNull(trigger);
                if(optionalAlert.isEmpty()){
                    if (!triggerState){
                        createAlert(trigger);
                    }
                }else{
                    Alert alert=optionalAlert.get();
                    if(alert.getStopDate()==null && triggerState){
                        alert.setStopDate(Instant.now());
                        alerter.printAlert(alert);
                        storage.saveAlert(alert);
                    }
                }
            } catch (CheckTriggerException e) {
                log.warn("Triggers::checkTriggers check trigger error "+trigger.getHost()+" name "+trigger.getName());
            } catch (MetricUnreachableException e) {
                log.warn("Triggers::checkTriggers metric unreachable "+trigger.getHost()+" name "+trigger.getName());
            }
        }
    }

    private void createAlert(Trigger trigger) {
            Alert alert = new Alert();
            alert.setTrigger(trigger);
            alert.setStartDate(Instant.now());
            alert.setOperationData("");

            alerter.printAlert(alert);

            storage.saveAlert(alert);
    }
}
