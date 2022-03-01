package ua.com.serverhelp.simplemonitoring.rest.controllers.api1.metric.exporter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entities.metric.Metric;
import ua.com.serverhelp.simplemonitoring.storage.Storage;
import ua.com.serverhelp.simplemonitoring.utils.MYLog;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/apiv1/metric/exporter/blackbox")
public class BlackBoxMetricRest extends AbstractMetricRest{
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
                    processItem("exporter." + proj + ".blackbox." + siteId + "." + input);
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

    private void checkAdditionalConditions(String pathPart) {
        //create trigger for LA
        Metric probeSuccess=storage.getOrCreateMetric(pathPart+"probe_success");
        storage.createIfNotExistTrigger(pathPart+"probe_success","ua.com.serverhelp.simplemonitoring.entities.trigger.Last15minValuesChecker",storage.getOrCreateParameterGroup(probeSuccess,"{}"));
        storage.createIfNotExistTrigger(pathPart+"probe_success","ua.com.serverhelp.simplemonitoring.entities.trigger.BooleanChecker",storage.getOrCreateParameterGroup(probeSuccess,"{}"));
    }

    @Override
    protected String[] getAllowedMetrics() {
        return new String[]{
                "probe_success",
                "probe_http_ssl",
                "probe_http_status_code",
                "probe_duration_seconds"
        };
    }
}
