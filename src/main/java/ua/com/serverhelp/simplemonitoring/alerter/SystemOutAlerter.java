package ua.com.serverhelp.simplemonitoring.alerter;

import ua.com.serverhelp.simplemonitoring.utils.MYLog;

//@Component //for test
public class SystemOutAlerter implements Alerter{
    @Override
    public void printMessage(String message) {
        MYLog.printAnywhere("SystemOutAlerter::printMessage "+message);
    }

    @Override
    public void printInfo(String message) {
        MYLog.printAnywhere("SystemOutAlerter::printInfo "+message);
    }
}
