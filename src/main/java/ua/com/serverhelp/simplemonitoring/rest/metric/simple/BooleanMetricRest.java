package ua.com.serverhelp.simplemonitoring.rest.metric.simple;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.rest.metric.AbstractMetricRest;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/metric/simple/boolean")
public class BooleanMetricRest extends AbstractMetricRest {
//    @Autowired
//    private MemoryMetricsQueue memoryMetricsQueue;
//    @Autowired
//    private TriggerRepository triggerRepository;

    @GetMapping("/")
    @ResponseBody
    public ResponseEntity<String> getAddEvent(
            @RequestHeader("X-Simple-Token") UUID token,
            @RequestParam String path,
            @RequestParam(defaultValue = "Boolean trigger on %s") String triggerName,
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
        //createTriggerIfNotExist(path, triggerName);
        log.debug("BooleanMetricRest::getAddEvent /api/v1/metric/boolean Event add:" + value);

        return ResponseEntity.ok().body("Success");
    }

    /*private void createTriggerIfNotExist(String path, String triggerName) {
        String id = DigestUtils.md5DigestAsHex((path + "{}").getBytes());
        Optional<Trigger> optionalTrigger = triggerRepository.findById(id);
        if (optionalTrigger.isEmpty()) {
            Trigger trigger = new Trigger();

            trigger.setId(id);
            trigger.setTriggerId(path);
            trigger.setName(String.format(triggerName, path));
            trigger.setDescription("Check last value to true or false");
            trigger.setPriority(TriggerPriority.HIGH);
            trigger.setConf(String.format("{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.CompareDoubleExpression\",\"parameters\":{\"operation\":\"<\",\"arg2\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ReadLastValueOfMetricExpression\",\"parameters\":{\"metricsDirectory\":\"%s\",\"metricName\":\"%s\",\"parameterGroup\":\"%s\"}},\"arg1\":{\"class\":\"ua.com.serverhelp.simplemetricstoragefile.entities.triggers.expressions.ConstantDoubleExpression\",\"parameters\":{\"value\":0.5}}}}", dirName, path, "{}"));

            triggerRepository.save(trigger);
        }
    }*/
}
