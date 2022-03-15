package ua.com.serverhelp.simplemonitoring.queue;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import ua.com.serverhelp.simplemonitoring.queue.itemprocessor.ItemProcessor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Data
@ToString
public class QueueElement{
    private String path;
    private String json;
    private Instant timestamp;
    private Double value;
    private List<ItemProcessor> itemProcessors=new ArrayList<>();

    public QueueElement(String path, String json, Instant timestamp, Double value) {
        this.path = path;
        this.json = json;
        this.timestamp = timestamp;
        this.value = value;
    }

    public void addItemProcessor(ItemProcessor itemProcessor){
        itemProcessors.add(itemProcessor);
    }

    /**
     * Run modificator processors
     *
     * @return true if you need to add element to db
     */
    public boolean runProcessors(){
        boolean ret=true;
        for (ItemProcessor itemProcessor:itemProcessors){
            log.info("Before "+itemProcessor.getClass().getSimpleName()+" "+this.toString());
            if(!itemProcessor.runProcessor(this)) ret=false;
            log.info("After "+itemProcessor.getClass().getSimpleName()+" "+this.toString());
        }
        return ret;
    }

    public QueueElement clone(){
        return new QueueElement(path, json, timestamp, value.doubleValue());
    }

}
