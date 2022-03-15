package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric.exporter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entities.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/apiv1/metric/exporter/node")
public class NodeMetricRest extends AbstractMetricRest{
    @Autowired
    private Storage storage;

    @PostMapping("/")
    public ResponseEntity<String> receiveData(
            @RequestHeader("X-Project") String proj,
            @RequestHeader("X-Hostname") String hostname,
            @RequestBody String data
    ) {
        Instant timestamp=Instant.now();
        String inputData= URLDecoder.decode(data, StandardCharsets.UTF_8);
        String[] inputs=inputData.split("\n");

        for (String input:inputs){
            if(isAllowedMetric(input)){
                try {
                    getInputQueue().add(timestamp+";exporter."+proj+"."+hostname+".node."+input.replace("node_", ""));
                }catch (NumberFormatException e){
                    log.warn("NodeMetricRest::receiveData number format error "+input);
                    return ResponseEntity.badRequest().body("number format error "+input);
                }catch (IllegalStateException | IndexOutOfBoundsException e){
                    log.warn("NodeMetricRest::receiveData regexp match error "+input);
                    return ResponseEntity.badRequest().body("regexp match error "+input);
                }
            }
        }
        //add triggers and calculate metrics
        createTriggersByHost("exporter."+proj+"."+hostname+".node.");

        return ResponseEntity.ok().body("Success");
    }

    @Override
    protected void createTriggers(String pathPart) {
        /*
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
         */
    }

    @Override
    protected String[] getAllowedMetrics() {
        return new String[]{
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
    }

    @Override
    protected String[] getDiffMetrics() {
        return new String[]{
                "cpu_seconds_total",
                "network_transmit_bytes_total",
                "network_receive_bytes_total"
        };
    }

    @Override
    protected Map<String, String> getAvgMetrics() {
        return Map.of("cpu_seconds_total","cpu");
    }
}
