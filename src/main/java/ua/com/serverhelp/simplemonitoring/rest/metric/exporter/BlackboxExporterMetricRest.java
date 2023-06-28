package ua.com.serverhelp.simplemonitoring.rest.metric.exporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerPriority;
import ua.com.serverhelp.simplemonitoring.rest.metric.AbstractMetricRest;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/metric/exporter/blackbox")
@RequiredArgsConstructor
public class BlackboxExporterMetricRest extends AbstractMetricRest {
    @PostMapping("/")
    public ResponseEntity<String> receiveData(
            @RequestHeader("X-Project") String proj,
            @RequestHeader("X-Site-Id") String siteId,
            @RequestHeader("X-Simple-Token") UUID token,
            @RequestBody String data
    ) {
        var organization = getOrganization(token);
        var now = Instant.now();
        var inputData = URLDecoder.decode(data, StandardCharsets.UTF_8);

        Arrays.stream(inputData.split("\n"))
                .filter(this::isValidMetric)
                .forEach(s -> {
                    try {
                        processItem(organization, now, "exporter." + proj + ".blackbox." + siteId + "." + s);
                    } catch (JsonProcessingException e) {
                        log.warn("BlackboxExporterMetricRest::receiveData JSON parse error " + s, e);
                    }
                });

        log.debug("BlackboxExporterMetricRest::receiveData /api/v1/metric/exporter/blackbox Event add:" + proj + "." + siteId);

        return ResponseEntity.ok("Success");
    }

    private void processItem(Organization organization, Instant now, String input) throws JsonProcessingException {
        //exporter.testproj.blackbox.https_example_com.probe_http_status_code 200
        var parts = input.split(" ");
        log.debug(input);
        checkIfNotExistTrigger(organization, parts[0]);

        var dataItem = DataItem.builder()
                .organization(organization)
                .path(parts[0])
                .parameters("{}")
                .timestamp(now)
                .value(Double.parseDouble(parts[1]))
                .build();

        log.debug("BlackboxExporterMetricRest::processItem Add item " + dataItem.getTimestamp() + " " + dataItem.getPath() + "  " + dataItem.getParameters() + "  " + dataItem.getValue());

        dataItemsService.putDataItem(dataItem);
    }

    private boolean isValidMetric(String metric) {
        if (metric.charAt(0) == '#') {
            return false;
        }

        String[] allowedMetrics = new String[]{
                ".*probe_success.*",
                ".*probe_http_ssl.*",
                ".*probe_http_status_code.*",
                ".*probe_duration_seconds.*"
        };
        for (String metricRegexp : allowedMetrics) {
            if (metric.matches(metricRegexp)) {
                return true;
            }
        }

        return false;
    }

    private void checkIfNotExistTrigger(Organization organization, String path) throws JsonProcessingException {
        if (path.matches("exporter.*.blackbox.*.probe_success")) {
            triggerService.createTriggerIfNotExistCompareItemToConst(
                    organization,
                    path + ".webcheck",
                    path,
                    "{}",
                    "Web check %s",
                    TriggerPriority.HIGH,
                    1.0,
                    "=="
            );
            triggerService.createTriggerIfNotExistCheckLastTimeItems(
                    organization,
                    path + ".last15min",
                    path,
                    "{}",
                    "Web check not receive data 15 min on %s",
                    TriggerPriority.HIGH,
                    Duration.of(15, ChronoUnit.MINUTES)
            );
        }
    }

}
