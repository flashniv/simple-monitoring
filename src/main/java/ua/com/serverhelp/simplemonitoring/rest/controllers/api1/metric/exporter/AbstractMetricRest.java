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
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class AbstractMetricRest {
    @Autowired
    private MetricsQueue metricsQueue;
    private static final ArrayList<String> triggers=new ArrayList<>();
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

    private void setItemProcessors(QueueElement queueElement) {
        //Diff metrics
        for (String metricExp:getDiffMetrics()){
            if(queueElement.getPath().contains(metricExp)){
                queueElement.addItemProcessor(new DiffItemProcessor());
            }
        }
        //Avg metrics
        for (Map.Entry<String,String> avgMetric:getAvgMetrics().entrySet()){
            if(queueElement.getPath().contains(avgMetric.getKey())){
                JSONObject parameters=new JSONObject(queueElement.getJson());
                parameters.remove(avgMetric.getValue());
                queueElement.addItemProcessor(new AvgItemProcessor(queueElement.getPath(), parameters.toString(), queueElement.getTimestamp()));
            }
        }
    }

    protected abstract void createTriggers(String pathPart);

    protected String[] getAllowedMetrics(){
        return new String[]{};
    }
    protected String[] getDiffMetrics(){
        return new String[]{};
    }
    protected Map<String,String> getAvgMetrics(){
        return Map.of();
    }

    protected void createTriggersByHost(String host){
        //TODO Restore trigger cache
        //if(!triggers.contains(host)){
            createTriggers(host);
        //    triggers.add(host);
        //}
    }
}
