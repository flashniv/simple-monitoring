package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric.exporter;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.MYLog;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/apiv1/metric/exporter/blackbox")
public class BlackBoxMetricRest {
    @Autowired
    private MetricsQueue metricsQueue;
    @Autowired
    private Storage storage;

    @PostMapping("/")
    public ResponseEntity<String> receiveData(
            @RequestHeader("X-Project") String proj,
            @RequestHeader("X-Site-Id") String siteId,
            @RequestBody String data
    ) {
        String inputData = URLDecoder.decode(data, StandardCharsets.UTF_8);
        String[] inputs = inputData.split("\n");

        for (String input : inputs) {
            if (isAllowedMetric(input)) {
                try {
                    processItem(proj, siteId, input);
                } catch (NumberFormatException e) {
                    MYLog.printWarn("NodeMetricRest::receiveData number format error " + input);
                    return ResponseEntity.badRequest().body("number format error " + input);
                } catch (IllegalStateException | IndexOutOfBoundsException e) {
                    MYLog.printWarn("NodeMetricRest::receiveData regexp match error " + input);
                    return ResponseEntity.badRequest().body("regexp match error " + input);
                }
            }
        }
        //add triggers and calculate metrics
        checkAdditionalConditions("exporter." + proj + ".blackbox." + siteId + ".");

        return ResponseEntity.ok().body("Success");
    }

    private void processItem(String proj, String siteId, String input) throws IllegalStateException, IndexOutOfBoundsException, NumberFormatException {
        Double value;
        String parameters = "";
        //probe_dns_lookup_time_seconds 0.068937512
        input = input.replace("\r", "");
        input = "exporter." + proj + ".blackbox." + siteId + "." + input;
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

    private void checkAdditionalConditions(String pathPart) {
        //create trigger for LA
        Metric probeSuccess=storage.getOrCreateMetric(pathPart+"probe_success");
        storage.createIfNotExistTrigger(pathPart+"probe_success","ua.com.serverhelp.simplemonitoring.entities.trigger.Last15minValuesChecker",storage.getOrCreateParameterGroup(probeSuccess,"{}"));
        storage.createIfNotExistTrigger(pathPart+"probe_success","ua.com.serverhelp.simplemonitoring.entities.trigger.BooleanChecker",storage.getOrCreateParameterGroup(probeSuccess,"{}"));
    }

    private String parseParameterGroup(String part) throws IllegalStateException, IndexOutOfBoundsException {
        JSONObject json = new JSONObject();
        String[] parameters = part.split(",");
        for (String parameter : parameters) {
            Pattern pattern = Pattern.compile("(.*)=\"(.*)\"");
            Matcher matcher = pattern.matcher(parameter);
            if (matcher.matches()) {
                json.put(matcher.group(1), matcher.group(2));
            }
        }
        return json.toString();
    }

    private boolean isAllowedMetric(String metric) {
        String[] allowedMetrics = {
                "probe_success",
                "probe_http_ssl",
                "probe_http_status_code",
                "probe_duration_seconds"
        };
        for (String metricExp : allowedMetrics) {
            if (metric.contains(metricExp)) {
                return true;
            }
        }
        return false;
    }

    private String getOptionsByMetric(String metric) {
        JSONObject res = new JSONObject();
//        String[] diffMetrics = {
//                "cpu_seconds_total",
//                "network_transmit_bytes_total",
//                "network_receive_bytes_total"
//        };
//        for (String metricExp:diffMetrics){
//            if(metric.contains(metricExp)){
//                res.put("diff", true);
//            }
//        }
        return res.toString();
    }
}
