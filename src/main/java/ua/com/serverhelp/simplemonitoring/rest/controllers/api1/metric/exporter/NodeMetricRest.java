package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric.exporter;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.MYLog;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/apiv1/metric/exporter/node")
public class NodeMetricRest {
    @Autowired
    private MetricsQueue metricsQueue;
    @Autowired
    private Storage storage;

    @PostMapping("/")
    public ResponseEntity<String> receiveData(
            @RequestHeader("X-Project") String proj,
            @RequestHeader("X-Hostname") String hostname,
            @RequestBody String data
    ) {
        String inputData= URLDecoder.decode(data, StandardCharsets.UTF_8);
        String[] inputs=inputData.split("\n");

        for (String input:inputs){
            if(isAllowedMetric(input)){
                try {
                    processItem(proj,hostname,input);
                }catch (NumberFormatException e){
                    MYLog.printWarn("NodeMetricRest::receiveData number format error "+input);
                    return ResponseEntity.badRequest().body("number format error "+input);
                }catch (IllegalStateException | IndexOutOfBoundsException e){
                    MYLog.printWarn("NodeMetricRest::receiveData regexp match error "+input);
                    return ResponseEntity.badRequest().body("regexp match error "+input);
                }
            }
        }
        //add triggers and calculate metrics
        checkAdditionalConditions("exporter."+proj+"."+hostname+".node.");

        return ResponseEntity.ok().body("Success");
    }

    private void processItem(String proj,String hostname,String input) throws IllegalStateException,IndexOutOfBoundsException,NumberFormatException{
        Double value;
        String parameters="";
        //node_cpu_seconds_total{cpu="0",mode="system"} 18.61
        input=input.replace("\r", "");
        input=Pattern.compile("([a-z]+)_(.*)").matcher(input).replaceFirst("exporter."+proj+"."+hostname+".$1.$2");
        input=Pattern.compile("(.*[0-9]e) ([0-9]+)$").matcher(input).replaceFirst("$1+$2");
        String[] parts;
        Pattern p = Pattern.compile("(.*)\\{(.*)} (.*)");
        Matcher m = p.matcher(input);
        if(m.matches()){
            parts=new String[3];
            parts[0]=m.group(1);
            parameters=m.group(2);
            parts[2]=m.group(3);
        }else{
            parts=input.split(" ");
        }
        value=Double.valueOf(parts[parts.length-1]);

        metricsQueue.putData(parts[0],parseParameterGroup(parameters),getOptionsByMetric(parts[0]), Instant.now(), value);
    }

    private void checkAdditionalConditions(String pathPart) {
        //create sum metric for cpu
        List<String> cpuModes = List.of("idle","iowait","irq","nice","softirq","steal","system","user");
        for (String cpuMode : cpuModes) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cpu", "%");
            jsonObject.put("mode", cpuMode);
            storage.createIfNotExistCalculateParameterGroup(pathPart+"cpu_seconds_total", jsonObject.toString(), "sum");
        }
        //create trigger for LA
        Metric load15=storage.getOrCreateMetric(pathPart+"load15");
        storage.createIfNotExistTrigger(pathPart+"load15","ua.com.serverhelp.simplemonitoring.entities.trigger.LoadAvgChecker",storage.getOrCreateParameterGroup(load15,"{}"));
        storage.createIfNotExistTrigger(pathPart+"load15","ua.com.serverhelp.simplemonitoring.entities.trigger.Last15minValuesChecker",storage.getOrCreateParameterGroup(load15,"{}"));
        //create DF trigger
        Metric filesystemSizeBytes= storage.getOrCreateMetric(pathPart+"filesystem_size_bytes");
        Metric filesystemAvailBytes= storage.getOrCreateMetric(pathPart+"filesystem_avail_bytes");
        List<ParameterGroup> sizeParameterGroupList=storage.getParameterGroups(filesystemSizeBytes);
        List<ParameterGroup> availParameterGroupList=storage.getParameterGroups(filesystemAvailBytes);
        for (int i=0;i< sizeParameterGroupList.size();i++) {
            storage.createIfNotExistTrigger(pathPart + "disk." +availParameterGroupList.get(i).getParameters().get("mountpoint"), "ua.com.serverhelp.simplemonitoring.entities.trigger.DiskFree85pChecker", availParameterGroupList.get(i), sizeParameterGroupList.get(i));
        }
        //Swap usage checker
        Metric swapSizeBytes= storage.getOrCreateMetric(pathPart+"memory_SwapTotal_bytes");
        Metric swapUsageBytes= storage.getOrCreateMetric(pathPart+"memory_SwapFree_bytes");
        List<ParameterGroup> swapSizeBytesGroupList=storage.getParameterGroups(swapSizeBytes);
        List<ParameterGroup> swapUsageBytesGroupList=storage.getParameterGroups(swapUsageBytes);
        storage.createIfNotExistTrigger(pathPart + "memory.swap", "ua.com.serverhelp.simplemonitoring.entities.trigger.SwapUsageChecker", swapUsageBytesGroupList.get(0), swapSizeBytesGroupList.get(0));
    }

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

    private boolean isAllowedMetric(String metric){
        String[] allowedMetrics={
                "node_load1",
                "node_load5",
                "node_load15",
                "node_memory_MemAvailable_bytes",
                "node_memory_MemTotal_bytes",
                "node_cpu_seconds_total",
                "node_filesystem_avail_bytes",
                "node_filesystem_size_bytes",
                "node_filesystem_files",
                "node_vmstat_pswp",
                "node_memory_Swap",
                "node_network_transmit_bytes_total",
                "node_network_receive_bytes_total"
        };
        for (String metricExp:allowedMetrics){
            if(metric.contains(metricExp)){
                return true;
            }
        }
        return false;
    }
    private String getOptionsByMetric(String metric) {
        JSONObject res=new JSONObject();
        String[] diffMetrics = {
                "cpu_seconds_total",
                "network_transmit_bytes_total",
                "network_receive_bytes_total"
        };
        for (String metricExp:diffMetrics){
            if(metric.contains(metricExp)){
                res.put("diff", true);
            }
        }
        return res.toString();
    }
}
