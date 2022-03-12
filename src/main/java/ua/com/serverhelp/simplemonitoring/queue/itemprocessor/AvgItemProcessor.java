package ua.com.serverhelp.simplemonitoring.queue.itemprocessor;

import lombok.extern.slf4j.Slf4j;
import ua.com.serverhelp.simplemonitoring.queue.QueueElement;

import java.util.HashMap;
import java.util.concurrent.Semaphore;

@Slf4j
public class AvgItemProcessor implements ItemProcessor {
    private static final Semaphore semaphore = new Semaphore(1);
    private static final HashMap<String, HashMap<String, Object>> items = new HashMap<>();

    public AvgItemProcessor(String path, String parameterGroup, Double value) {
        try {
            semaphore.acquire();
            HashMap<String, Object> item = items.get(path + parameterGroup);
            if (item == null) {
                item = new HashMap<>();
                item.put("path", path);
                item.put("parameterGroup", parameterGroup);
                item.put("sum", value);
                item.put("count", 1L);
                items.put(path + parameterGroup, item);
            } else {
                item.put("sum", (Double) item.get("sum") + value);
                item.put("count", ((Long) item.get("count")) + 1L);
            }
        } catch (Exception e) {
            log.warn("Semaphore not ack",e);
        } finally {
            semaphore.release();
        }
    }

    @Override
    public boolean runProcessor(QueueElement queueElement) {
        return false;
    }
}
