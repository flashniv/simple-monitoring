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
import java.util.concurrent.Semaphore;

@Slf4j
@Component
public class MemoryMetricsQueue implements MetricsQueue {
    @Autowired
    private Storage storage;
    @Autowired
    private HealthMetrics healthMetrics;
    private final ArrayList<Event> events=new ArrayList<>();
    private final HashMap<String,QueueParameter> prevValues=new HashMap<>();
    private final Semaphore eventsSemaphore=new Semaphore(1);

    @Override
    public List<Event> getEvents() {
        List<Event> eventList=null;
        try {
            eventsSemaphore.acquire();

            eventList=(List<Event>) events.clone();
            events.clear();
        }catch (InterruptedException e){
            log.error("acquire semaphore exception",e);
        } finally {
            eventsSemaphore.release();
        }
        return eventList;
    }

    @Override
    public void putData(String path,String json,String options, Instant timestamp, Double value) {
        log.info("start putData "+path+" "+json+" "+Instant.now());
        try {
            Metric metric= storage.getOrCreateMetric(path);
            ParameterGroup parameterGroup= storage.getOrCreateParameterGroup(metric,json);

            value=checkOptions(path,json,options,timestamp,value);

            Event event=new Event();
            event.setParameterGroup(parameterGroup);
            event.setTimestamp(timestamp);
            event.setValue(value);

            healthMetrics.incEventCount();

            eventsSemaphore.acquire();
            events.add(event);
        }catch (InterruptedException e){
            log.error("acquire semaphore exception",e);
        } finally {
            eventsSemaphore.release();
        }
        log.info("end putData "+path+" "+json+" "+Instant.now());
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
