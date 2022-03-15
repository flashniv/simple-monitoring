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
            //log.info("new "+queueElement);
            //log.info("prev "+prevValue);
            Duration duration=Duration.between(prevValue.getTimestamp(), queueElement.getTimestamp());
            //log.info("dur "+prevValue.getTimestamp()+" "+ queueElement.getTimestamp()+"  res "+duration.toMillis()/1000.0);
            res=(queueElement.getValue()-prevValue.getValue())/(duration.toMillis()/1000.0);
        }
        prevValues.put(queueElement.getPath()+queueElement.getJson(),queueElement.clone());

        queueElement.setValue(res);

        return true;
    }
}
