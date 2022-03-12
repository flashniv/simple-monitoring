package ua.com.serverhelp.simplemonitoring.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemonitoring.entities.event.Event;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.HealthMetrics;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
public class MemoryMetricsQueue implements MetricsQueue {
    @Autowired
    private Storage storage;
    @Autowired
    private HealthMetrics healthMetrics;
    private final ConcurrentLinkedQueue<QueueElement> linkedQueue=new ConcurrentLinkedQueue<>();

    @Override
    public List<Event> getEvents() {
        ArrayList<Event> events=new ArrayList<>();
        while(!linkedQueue.isEmpty()){
            QueueElement queueElement=linkedQueue.poll();
            Metric metric= storage.getOrCreateMetric(queueElement.getPath());
            ParameterGroup parameterGroup= storage.getOrCreateParameterGroup(metric, queueElement.getJson());

            Event event=new Event();
            event.setParameterGroup(parameterGroup);
            event.setTimestamp(queueElement.getTimestamp());
            event.setValue(queueElement.getValue());

            healthMetrics.incEventCount();

            events.add(event);
        }
        return events;
    }

    @Override
    public void putData(QueueElement queueElement) {
        linkedQueue.add(queueElement);
    }
}
