package ua.com.serverhelp.simplemonitoring.alerter;

import java.io.IOException;

public interface AlertSender {
    boolean sendMessage(String text) throws IOException;
}
