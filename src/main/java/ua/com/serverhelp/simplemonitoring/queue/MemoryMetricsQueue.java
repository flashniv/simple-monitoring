package ua.com.serverhelp.simplemonitoring.queue;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.com.serverhelp.simplemonitoring.entities.event.Event;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.HealthMetrics;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
public class MemoryMetricsQueue implements MetricsQueue {
    @Autowired
    private Storage storage;
    @Autowired
    private HealthMetrics healthMetrics;
    private final HashMap<String,QueueParameter> prevValues=new HashMap<>();
    private final ConcurrentLinkedQueue<QueueElement> linkedQueue=new ConcurrentLinkedQueue<>();

    @Override
    public List<Event> getEvents() {
        ArrayList<Event> events=new ArrayList<>();
        while(!linkedQueue.isEmpty()){
            QueueElement queueElement=linkedQueue.poll();
            Metric metric= storage.getOrCreateMetric(queueElement.getPath());
            ParameterGroup parameterGroup= storage.getOrCreateParameterGroup(metric, queueElement.getJson());

            queueElement.setValue(checkOptions(queueElement.getPath(), queueElement.getJson(), queueElement.getOptions(), queueElement.getTimestamp(), queueElement.getValue()));

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
    public void putData(String path,String json,String options, Instant timestamp, Double value) {
        QueueElement queueElement=new QueueElement();
        queueElement.setPath(path);
        queueElement.setJson(json);
        queueElement.setTimestamp(timestamp);
        queueElement.setOptions(options);
        queueElement.setValue(value);
        linkedQueue.add(queueElement);
    }

    private Double checkOptions(String path, String json, String options, Instant timestamp, Double value) {
        Double res=value;
        JSONObject optionsJSON=new JSONObject(options);
        if (optionsJSON.has("diff")){
            if(prevValues.containsKey(path+json)){
                QueueParameter prev=prevValues.get(path+json);
                Duration duration=Duration.between(prev.getTimestamp(), timestamp);
                res=(value-prev.getValue())/ (duration.toNanos()/1000000.0);
            }else{
                res=0.0;
            }
            prevValues.put(path+json, new QueueParameter(timestamp, value));
        }
        return res;
    }
}
