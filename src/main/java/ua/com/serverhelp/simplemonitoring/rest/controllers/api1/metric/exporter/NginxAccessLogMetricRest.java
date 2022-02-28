package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric.exporter;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.queue.MetricsQueue;
import ua.com.serverhelp.simplemonitoring.storage.Storage;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/apiv1/metric/exporter/nginx-access-log")
public class NginxAccessLogMetricRest {
    @Autowired
    private MetricsQueue metricsQueue;
    @Autowired
    private Storage storage;

    @PostMapping("/")
    public ResponseEntity<String> receiveData(
            @RequestBody String data
    ) {
        String inputData = URLDecoder.decode(data, StandardCharsets.UTF_8);
        String[] inputs = inputData.split("\n");

        for (String input : inputs) {
            if (isAllowedMetric(input)) {
                try {
                    processItem(input);
                } catch (NumberFormatException e) {
                    log.warn("NodeMetricRest::receiveData number format error " + input);
                    return ResponseEntity.badRequest().body("number format error " + input);
                } catch (IllegalStateException | IndexOutOfBoundsException e) {
                    log.warn("NodeMetricRest::receiveData regexp match error " + input);
                    return ResponseEntity.badRequest().body("regexp match error " + input);
                }
            }
        }

        return ResponseEntity.ok().body("Success");
    }

    private void processItem(String input) throws IllegalStateException, IndexOutOfBoundsException, NumberFormatException {
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

        if(parameters.matches("method=\"[A-Z]+\",code=\"500\"")){
            //add triggers and calculate metrics
            checkAdditionalConditions(parts[0],parseParameterGroup(parameters));
        }

        metricsQueue.putData(parts[0], parseParameterGroup(parameters), getOptionsByMetric(parts[0]), Instant.now(), value);
    }

    private void checkAdditionalConditions(String pathPart, String parameterGroup) {
        //create trigger for LA
        Metric metric=storage.getOrCreateMetric(pathPart);
        storage.createIfNotExistTrigger(pathPart,"ua.com.serverhelp.simplemonitoring.entities.trigger.NginxAccessLog500Checker",storage.getOrCreateParameterGroup(metric,parameterGroup));
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
        return metric.matches("nginx_access_log.*method=.*,code=.*");
    }

    private String getOptionsByMetric(String metric) {
        JSONObject res = new JSONObject();
        return res.toString();
    }
}
