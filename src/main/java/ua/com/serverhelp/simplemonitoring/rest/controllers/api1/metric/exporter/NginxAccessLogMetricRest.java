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
public class NginxAccessLogMetricRest extends AbstractMetricRest{
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
//                    if (parameters.matches("method=\"[A-Z]+\",code=\"500\"")) {
//                        //add triggers and calculate metrics
//                        checkAdditionalConditions(parts[0], parseParameterGroup(parameters));
//                    }

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

    private void checkAdditionalConditions(String pathPart, String parameterGroup) {
        //create trigger for LA
        Metric metric=storage.getOrCreateMetric(pathPart);
        storage.createIfNotExistTrigger(pathPart,"ua.com.serverhelp.simplemonitoring.entities.trigger.NginxAccessLog500Checker",storage.getOrCreateParameterGroup(metric,parameterGroup));
    }

    @Override
    protected String[] getAllowedMetrics() {
        return new String[]{
                "nginx_access_log"
        };
    }
}
