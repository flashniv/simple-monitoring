package ua.com.serverhelp.simplemonitoring.queue.itemprocessor;

import lombok.extern.slf4j.Slf4j;
import ua.com.serverhelp.simplemonitoring.queue.QueueElement;

import java.time.Duration;
import java.util.HashMap;

@Slf4j
public class DiffItemProcessor implements ItemProcessor{
    private static final HashMap<String,QueueElement> prevValues=new HashMap<>();
    @Override
    public boolean runProcessor(QueueElement queueElement) {
        double res=0.0;
        if(prevValues.containsKey(queueElement.getPath()+queueElement.getJson())){
            QueueElement prevValue=prevValues.get(queueElement.getPath()+queueElement.getJson());
            Duration duration=Duration.between(prevValue.getTimestamp(), queueElement.getTimestamp());
            res=(queueElement.getValue()-prevValue.getValue())/ (duration.toNanos()/1000000.0);
        }
        prevValues.put(queueElement.getPath()+queueElement.getJson(),queueElement);

        queueElement.setValue(res);

        log.info("Metric "+queueElement.getPath()+queueElement.getJson()+" value:"+res);

        return true;
    }
}
