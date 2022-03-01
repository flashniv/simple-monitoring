package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric.exporter;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractMetricRest {
    @Autowired
    private MetricsQueue metricsQueue;

    private String parseParameterGroup(String part) throws IllegalStateException,IndexOutOfBoundsException{
        JSONObject json=new JSONObject();
        String[] parameters=part.split(",");
        for(String parameter:parameters){
            Pattern pattern=Pattern.compile("(.*)=\"(.*)\"");
            Matcher matcher=pattern.matcher(parameter);
            if(matcher.matches()){
                json.put(matcher.group(1), matcher.group(2));
            }
        }
        return json.toString();
    }

    protected void processItem(String input) throws IllegalStateException, IndexOutOfBoundsException, NumberFormatException {
        Double value;
        String parameters = "";
        input = input.replace("\r", "");
        input = Pattern.compile("(.*[0-9]e) ([0-9]+)$").matcher(input).replaceFirst("$1+$2");
        String[] parts;
        Pattern p = Pattern.compile("(.*)\\{(.*)} (.*)");
        Matcher m = p.matcher(input);
        if (m.matches()) {
            parts = new String[3];
            parts[0] = m.group(1);
            parameters = m.group(2);
            parts[2] = m.group(3);
        } else {
            parts = input.split(" ");
        }
        value = Double.valueOf(parts[parts.length - 1]);

        metricsQueue.putData(parts[0], parseParameterGroup(parameters), getOptionsByMetric(parts[0]), Instant.now(), value);
    }

    protected boolean isAllowedMetric(String metric){
        for (String metricExp:getAllowedMetrics()){
            if(metric.contains(metricExp)){
                return true;
            }
        }
        return false;
    }
    private String getOptionsByMetric(String metric) {
        JSONObject res=new JSONObject();
        for (String metricExp:getDiffMetrics()){
            if(metric.contains(metricExp)){
                res.put("diff", true);
            }
        }
        return res.toString();
    }

    protected String[] getAllowedMetrics(){
        return new String[]{};
    }
    protected String[] getDiffMetrics(){
        return new String[]{};
    }

}
