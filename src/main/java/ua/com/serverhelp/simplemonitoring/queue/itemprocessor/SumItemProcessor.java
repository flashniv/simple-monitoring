package ua.com.serverhelp.simplemonitoring.queue.itemprocessor;

import io.sentry.Sentry;
import lombok.extern.slf4j.Slf4j;
import ua.com.serverhelp.simplemonitoring.queue.QueueElement;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

@Slf4j
public class SumItemProcessor implements ItemProcessor {
    private final String path;
    private final String parameterGroup;
    private final Instant timestamp;
    private static final Semaphore semaphore = new Semaphore(1);
    private static HashMap<String, HashMap<String, Object>> items = new HashMap<>();

    public SumItemProcessor(String path, String parameterGroup, Instant timestamp) {
        this.path=path;
        this.parameterGroup=parameterGroup;
        this.timestamp = timestamp;
    }

    public static List<QueueElement> getQueueElements(){
        List<QueueElement> queueElements = new ArrayList<>();
        try {
            semaphore.acquire();
            for (String key : items.keySet()) {
                HashMap<String, Object> item = items.get(key);
                QueueElement queueElement = new QueueElement("" + item.get("path"), "" + item.get("parameterGroup"),(Instant) item.get("timestamp"), (Double) item.get("sum"));
                queueElements.add(queueElement);
            }
            items=new HashMap<>();
        }catch (Exception e){
            Sentry.captureException(e);
            log.warn("Semaphore not ack",e);
        }finally {
            semaphore.release();
        }
        return queueElements;
    }

    @Override
    public boolean runProcessor(QueueElement queueElement) {
        try {
            semaphore.acquire();
            HashMap<String, Object> item = items.get(path + parameterGroup);
            if (item == null) {
                item = new HashMap<>();
                item.put("path", path);
                item.put("parameterGroup", parameterGroup);
                item.put("sum", queueElement.getValue());
                item.put("timestamp", timestamp);
                items.put(path + parameterGroup, item);
            } else {
                item.put("sum", (Double) item.get("sum") + queueElement.getValue());
            }
        } catch (Exception e) {
            Sentry.captureException(e);
            log.warn("Semaphore not ack",e);
        } finally {
            semaphore.release();
        }
        return false;
    }
}
