package ua.com.serverhelp.simplemonitoring.rest.metric.simple;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerPriority;
import ua.com.serverhelp.simplemonitoring.rest.metric.AbstractMetricRest;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/metric/simple/boolean")
public class BooleanMetricRest extends AbstractMetricRest {

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<String> getAddEvent(
            @RequestHeader("X-Simple-Token") UUID token,
            @RequestParam String path,
            @RequestParam(defaultValue = "Boolean trigger on %s") String triggerName,
            @RequestParam(defaultValue = "HIGH") TriggerPriority priority,
            @RequestParam(defaultValue = "true") Boolean value
    ) {
        var organization = getOrganization(token);
        dataItemsService.putDataItem(DataItem.builder()
                .organization(organization)
                .path(path)
                .parameters("{}")
                .timestamp(Instant.now())
                .value(value ? 1.0 : 0.0)
                .build()
        );
        triggerService.createTriggerIfNotExistCompareItemToConst(organization,path+"{}.boolean", path, "{}", triggerName, priority, 1.0,"==");
        log.debug("BooleanMetricRest::getAddEvent /api/v1/metric/boolean Event add:" + value);

        return ResponseEntity.ok().body("Success");
    }
}
