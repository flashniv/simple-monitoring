package ua.com.serverhelp.simplemonitoring.rest.metric.exporter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerPriority;
import ua.com.serverhelp.simplemonitoring.rest.metric.AbstractMetricRest;
import ua.com.serverhelp.simplemonitoring.service.TriggerService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/api/v1/metric/exporter/node")
@RequiredArgsConstructor
public class NodeExporterMetricRest extends AbstractMetricRest {
    private final Pattern replaceE = Pattern.compile("(.*[0-9]e) ([0-9]+)$");
    private final Pattern parametersSplitToGroup = Pattern.compile("(.*)=\"(.*)\"");
    private final TriggerService triggerService;

    @PostMapping("/")
    public ResponseEntity<String> receiveData(
            @RequestHeader("X-Project") String proj,
            @RequestHeader("X-Hostname") String hostname,
            @RequestHeader("X-Simple-Token") UUID token,
            @RequestBody String data
    ) {
        var organization = getOrganization(token);
        var now = Instant.now();
        var inputData = URLDecoder.decode(data, StandardCharsets.UTF_8);

        Arrays.stream(inputData.split("\n"))
                .filter(this::isValidMetric)
                .forEach(s -> processItem(organization, now, "exporter." + proj + "." + hostname + ".node." + s.replace("node_", "")));

        log.debug("NodeExporterMetricRest::receiveData /api/v1/metric/exporter/node Event add:" + proj + "." + hostname);

        return ResponseEntity.ok().body("Success");
    }

    private boolean isValidMetric(String metric) {
        if (metric.charAt(0) == '#') {
            return false;
        }

        String[] allowedMetrics = new String[]{
                ".*node_load1.*",
                ".*node_load5.*",
                ".*node_load15.*",
                ".*node_memory_Mem.*",
                ".*node_cpu_seconds_total.*",
                ".*node_filesystem_avail.*ext[3|4].*",
                ".*node_filesystem_free.*ext[3|4].*",
                ".*node_filesystem_size.*ext[3|4].*",
                ".*node_filesystem_files.*ext[3|4].*",
                ".*node_vmstat_pswp.*",
                ".*node_memory_Swap.*",
                ".*node_network_transmit_bytes(?!.*br-)(?!.*veth)(?!.*docker0).*",
                ".*node_network_receive_bytes(?!.*br-)(?!.*veth)(?!.*docker0).*"
        };
        for (String metricRegexp : allowedMetrics) {
            if (metric.matches(metricRegexp)) {
                return true;
            }
        }

        return false;
    }

    private void processItem(Organization organization, Instant now, String input) {
        try {
            input = input.replace("\r", "");
            input = replaceE.matcher(input).replaceFirst("$1+$2");
            if (input.contains("{")) {
                input = input.replace('{', ';').replace("} ", ";");
            } else {
                input = input.replace(" ", ";;");
            }
            String[] parts = input.split(";");
            String parameterGroup = parseParameterGroup(parts[1]);

            checkIfNotExistTrigger(organization, parts[0], parameterGroup);

            var dataItem = DataItem.builder()
                    .organization(organization)
                    .path(parts[0])
                    .parameters(parameterGroup)
                    .timestamp(now)
                    .value(Double.parseDouble(parts[2]))
                    .build();

            log.debug("Add item " + dataItem.getTimestamp() + " " + dataItem.getPath() + "  " + dataItem.getParameters() + "  " + dataItem.getValue());

            dataItemsService.putDataItem(dataItem);
        } catch (JsonProcessingException e) {
            log.warn("NodeExporterMetricRest::processItem error parsing item:" + input);
        }
    }

    private String parseParameterGroup(String part) throws IllegalStateException, IndexOutOfBoundsException, JsonProcessingException {
        var objectMapper = new ObjectMapper();
        var jsonNode = objectMapper.createObjectNode();

        String[] parameters = part.split(",");
        for (String parameter : parameters) {
            Matcher matcher = parametersSplitToGroup.matcher(parameter);
            if (matcher.matches()) {
                jsonNode.put(matcher.group(1), matcher.group(2));
            }
        }
        return objectMapper.writeValueAsString(jsonNode);
    }

    private void checkIfNotExistTrigger(Organization organization, String path, String parameters) throws JsonProcessingException {
        if (path.matches("exporter.*node.load15")) {
            triggerService.createTriggerIfNotExistCompareItemToConst(
                    organization,
                    path + "." + parameters + ".load-avg",
                    path,
                    parameters,
                    "Load average too high on %s",
                    TriggerPriority.HIGH,
                    8.0,
                    ">"
            );
            triggerService.createTriggerIfNotExistCheckLastTimeItems(
                    organization,
                    path + "." + parameters + ".down",
                    path,
                    parameters,
                    "Server was down %s",
                    TriggerPriority.HIGH,
                    Duration.of(15, ChronoUnit.MINUTES)
            );
        }
        if (path.matches("exporter.*.node.filesystem_size_bytes")) {
            triggerService.createTriggerIfNotExistCheckMetricsRatio(
                    organization,
                    path + "." + parameters + ".disk-free",
                    "Disk free space less than 15%% on " + path.replace(".node.filesystem_size_bytes", "") + parameters,
                    path.replace("filesystem_size_bytes", "filesystem_avail_bytes"),
                    parameters,
                    path,
                    parameters,
                    TriggerPriority.HIGH,
                    0.15
            );

        }
        if (path.matches("exporter.*.node.filesystem_size")) {
            triggerService.createTriggerIfNotExistCheckMetricsRatio(
                    organization,
                    path + "." + parameters + ".disk-free",
                    "Disk free space less than 15%% on " + path.replace(".node.filesystem_size", "") + parameters,
                    path.replace("filesystem_size", "filesystem_free"),
                    parameters,
                    path,
                    parameters,
                    TriggerPriority.HIGH,
                    0.15
            );

        }
        if (path.matches("exporter.*.node.filesystem_files")) {
            triggerService.createTriggerIfNotExistCheckMetricsRatio(
                    organization,
                    path + "." + parameters + ".inodes-free",
                    "Disk free Inodes less than 15%% on " + path.replace(".node.filesystem_files", "") + parameters,
                    path.replace("filesystem_files", "filesystem_files_free"),
                    parameters,
                    path,
                    parameters,
                    TriggerPriority.HIGH,
                    0.15
            );

        }
    }
}
