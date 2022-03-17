package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric.exporter;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;
import ua.com.serverhelp.simplemonitoring.queue.QueueElement;
import ua.com.serverhelp.simplemonitoring.queue.itemprocessor.AvgItemProcessor;
import ua.com.serverhelp.simplemonitoring.queue.itemprocessor.DiffItemProcessor;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class AbstractMetricRest {
    @Autowired
    private MetricsQueue metricsQueue;
    private final HashMap<String,Boolean> triggers=new HashMap<>();
    @Getter
    private final ConcurrentLinkedQueue<String> inputQueue=new ConcurrentLinkedQueue<>();
    private final Pattern replaceE=Pattern.compile("(.*[0-9]e) ([0-9]+)$");
    private final Pattern parametersSplitToGroup=Pattern.compile("(.*)=\"(.*)\"");

    private String parseParameterGroup(String part) throws IllegalStateException,IndexOutOfBoundsException{
        JSONObject json=new JSONObject();
        String[] parameters=part.split(",");
        for(String parameter:parameters){
            Matcher matcher=parametersSplitToGroup.matcher(parameter);
            if(matcher.matches()){
                json.put(matcher.group(1), matcher.group(2));
            }
        }
        return json.toString();
    }

    public void processItems(){
        while (!inputQueue.isEmpty()){
            processItem(inputQueue.poll());
        }
    }

    public void processTriggers(){
        for (Map.Entry<String,Boolean> entry:((HashMap<String,Boolean>)triggers.clone()).entrySet()){
            log.info("Trigger check "+entry.getKey());
            if(!entry.getValue()){
                if(createTriggers(entry.getKey())){
                    log.info("Trigger created "+entry.getKey());
                    triggers.put(entry.getKey(), true);
                }
            }
        }
    }

    private void processItem(String input) throws IllegalStateException, IndexOutOfBoundsException, NumberFormatException {
        input = input.replace("\r", "");
        input = replaceE.matcher(input).replaceFirst("$1+$2");
        if(input.contains("{")) {
            input = input.replace('{', ';').replace("} ", ";");
        }else{
            input = input.replace(" ", ";;");
        }
        String[] parts=input.split(";");
        //create response container
        QueueElement queueElement=new QueueElement(parts[1], parseParameterGroup(parts[2]), Instant.parse(parts[0]), Double.parseDouble(parts[3]));
        //add modificators to queue element
        setItemProcessors(queueElement);
        //run modificators
        if(queueElement.runProcessors()){
            metricsQueue.putData(queueElement);
        }
    }

    protected boolean isAllowedMetric(String metric){
        for (String metricExp:getAllowedMetrics()){
            if(metric.contains(metricExp)){
                return true;
            }
        }
        return false;
    }

    protected void setItemProcessors(QueueElement queueElement) {
    }

    protected abstract boolean createTriggers(String pathPart);

    protected String[] getAllowedMetrics(){
        return new String[]{};
    }

    protected void addTrigger(String host){
        if(!triggers.containsKey(host)){
            log.info("Trigger add "+host);
            triggers.put(host, false);
        }
    }
}
