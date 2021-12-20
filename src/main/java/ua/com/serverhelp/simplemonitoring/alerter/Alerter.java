package ua.com.serverhelp.simplemonitoring.alerter;

import ua.com.serverhelp.simplemonitoring.entities.alerts.Alert;

public interface Alerter {
    default void printAlert(Alert alert) {
        if(alert.getStopDate()==null){
            printMessage("<b>ERR "+alert.getTrigger().getName()+" in path "+alert.getTrigger().getHost()+"</b>\non event time "+alert.getStartDate());
        }else {
            printMessage("<b>OK "+alert.getTrigger().getName()+" in path "+alert.getTrigger().getHost()+"</b>\non event time "+alert.getStopDate());
        }
    }
    default void printAlert(String alertMessage) {
        printMessage("<b>ERR "+alertMessage+"</b>");
    }

    void printMessage(String message);
    void printInfo(String message);
}
