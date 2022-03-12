package ua.com.serverhelp.simplemonitoring.queue.itemprocessor;

import ua.com.serverhelp.simplemonitoring.queue.QueueElement;

public interface ItemProcessor {
    boolean runProcessor(QueueElement queueElement);
}
