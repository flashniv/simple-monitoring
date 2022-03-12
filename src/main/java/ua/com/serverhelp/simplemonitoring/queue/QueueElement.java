package ua.com.serverhelp.simplemonitoring.queue;

import lombok.Data;
import ua.com.serverhelp.simplemonitoring.queue.itemprocessor.ItemProcessor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
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
     * @return true if need to add element to db
     */
    public boolean runProcessors(){
        boolean ret=true;
        for (ItemProcessor itemProcessor:itemProcessors){
            if(!itemProcessor.runProcessor(this)) ret=false;
        }
        return ret;
    }
}
