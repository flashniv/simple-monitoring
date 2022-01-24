package ua.com.serverhelp.simplemonitoring.alerter;

import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemonitoring.entities.alerts.Alert;
import ua.com.serverhelp.simplemonitoring.utils.MYLog;

@Component
public class Alerter {
    public void printAlert(Alert alert) {
        if(alert.getStopDate()==null){
            printMessage("<b>ERR "+alert.getTrigger().getName()+" in path "+alert.getTrigger().getHost()+"</b>\non event time "+alert.getStartDate());
        }else {
            printMessage("<b>OK "+alert.getTrigger().getName()+" in path "+alert.getTrigger().getHost()+"</b>\non event time "+alert.getStopDate());
        }
    }
    public void printAlert(String alertMessage) {
        printMessage("<b>ERR "+alertMessage+"</b>");
    }

    private void printMessage(String message){
        MYLog.printAnywhere(message);
    }
}
