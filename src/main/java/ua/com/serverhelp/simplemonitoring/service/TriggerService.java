package ua.com.serverhelp.simplemonitoring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alert;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.triggers.Trigger;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerPriority;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerStatus;
import ua.com.serverhelp.simplemonitoring.repository.AlertRepository;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;
import ua.com.serverhelp.simplemonitoring.repository.TriggerRepository;
import ua.com.serverhelp.simplemonitoring.service.alert.AlertService;
import ua.com.serverhelp.simplemonitoring.service.cache.CacheService;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
public class TriggerService {
    @Autowired
    private ParameterGroupRepository parameterGroupRepository;
    @Autowired
    private TriggerRepository triggerRepository;
    @Autowired
    private AlertService alertService;
    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private CacheService cacheService;

    /**
     * Create if not exist trigger with preconfigured typical conf
     *
     * @param organization Organization object
     * @param triggerId    Text id path+params+modificator
     * @param path         Metric dotted path
     * @param parameters   Parameters for parameter group
     * @param triggerName  Text name for trigger
     * @param priority     Trigger priority
     * @param constant     const for compare
     */
    public void createTriggerIfNotExistCompareItemToConst(
            Organization organization,
            String triggerId,
            String path,
            String parameters,
            String triggerName,
            TriggerPriority priority,
            double constant,
            String operation
    ) throws JsonProcessingException {
        if (checkNotExist(organization, triggerId)) {
            var objectMapper = new ObjectMapper();
            var parameterGroup = parameterGroupRepository.getOrCreateParameterGroup(organization, path, parameters);

            var compareDoubleExpression = objectMapper.createObjectNode();
            var compareDoubleExpressionParameters = objectMapper.createObjectNode();
            var constantDoubleExpression = objectMapper.createObjectNode();
            var constantDoubleExpressionParameters = objectMapper.createObjectNode();
            var readValuesOfMetricExpressionParameters = objectMapper.createObjectNode();
            var readValuesOfMetricExpression = objectMapper.createObjectNode();

            constantDoubleExpressionParameters.put("value", constant);
            constantDoubleExpression.put("class", "ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression");
            constantDoubleExpression.set("parameters", constantDoubleExpressionParameters);

            readValuesOfMetricExpressionParameters.put("parameterGroup", String.valueOf(parameterGroup.getId()));
            readValuesOfMetricExpressionParameters.put("collectorClass", "ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.LastItemValueCollector");
            readValuesOfMetricExpressionParameters.put("organizationID", "__organizationID__");
            readValuesOfMetricExpressionParameters.put("beginDiff", 2592000L);
            readValuesOfMetricExpressionParameters.put("endDiff", 0L);

            readValuesOfMetricExpression.put("class", "ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ReadValuesOfMetricExpression");
            readValuesOfMetricExpression.set("parameters", readValuesOfMetricExpressionParameters);

            compareDoubleExpressionParameters.put("operation", operation);
            compareDoubleExpressionParameters.set("arg1", constantDoubleExpression);
            compareDoubleExpressionParameters.set("arg2", readValuesOfMetricExpression);

            compareDoubleExpression.put("class", "ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.CompareDoubleExpression");
            compareDoubleExpression.set("parameters", compareDoubleExpressionParameters);

            createTrigger(organization, triggerId, path, parameters, triggerName, priority, objectMapper.writeValueAsString(compareDoubleExpression));
        }
    }

    /**
     * Create if not exist trigger for check exist values in last N minutes
     *
     * @param organization Organization object
     * @param triggerId    Text id path+params+modificator
     * @param path         Metric dotted path
     * @param parameters   Parameters for parameter group
     * @param triggerName  Text name for trigger
     * @param priority     Trigger priority
     * @param duration     duration for checking values
     */
    public void createTriggerIfNotExistCheckLastTimeItems(
            Organization organization,
            String triggerId,
            String path,
            String parameters,
            String triggerName,
            TriggerPriority priority,
            Duration duration
    ) throws JsonProcessingException {
        if (checkNotExist(organization, triggerId)) {
            var objectMapper = new ObjectMapper();
            var parameterGroup = parameterGroupRepository.getOrCreateParameterGroup(organization, path, parameters);

            var compareDoubleExpression = objectMapper.createObjectNode();
            var compareDoubleExpressionParameters = objectMapper.createObjectNode();
            var constantDoubleExpression = objectMapper.createObjectNode();
            var constantDoubleExpressionParameters = objectMapper.createObjectNode();
            var timestampDoubleExpression = objectMapper.createObjectNode();
            var readValuesOfMetricExpressionParameters = objectMapper.createObjectNode();
            var readValuesOfMetricExpression = objectMapper.createObjectNode();
            var mathDoubleExpressionParameters = objectMapper.createObjectNode();
            var mathDoubleExpression = objectMapper.createObjectNode();

            timestampDoubleExpression.put("class", "ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.TimestampDoubleExpression");
            timestampDoubleExpression.set("parameters", objectMapper.createObjectNode());

            constantDoubleExpressionParameters.put("value", duration.getSeconds());
            constantDoubleExpression.put("class", "ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression");
            constantDoubleExpression.set("parameters", constantDoubleExpressionParameters);

            readValuesOfMetricExpressionParameters.put("parameterGroup", String.valueOf(parameterGroup.getId()));
            readValuesOfMetricExpressionParameters.put("collectorClass", "ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.LastItemTimestampCollector");
            readValuesOfMetricExpressionParameters.put("organizationID", "__organizationID__");
            readValuesOfMetricExpressionParameters.put("beginDiff", 2592000L);
            readValuesOfMetricExpressionParameters.put("endDiff", 0L);

            readValuesOfMetricExpression.put("class", "ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ReadValuesOfMetricExpression");
            readValuesOfMetricExpression.set("parameters", readValuesOfMetricExpressionParameters);

            mathDoubleExpressionParameters.put("operation", "-");
            mathDoubleExpressionParameters.set("arg1", timestampDoubleExpression);
            mathDoubleExpressionParameters.set("arg2", readValuesOfMetricExpression);

            mathDoubleExpression.put("class", "ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.MathDoubleExpression");
            mathDoubleExpression.set("parameters", mathDoubleExpressionParameters);

            compareDoubleExpressionParameters.put("operation", ">");
            compareDoubleExpressionParameters.set("arg1", constantDoubleExpression);
            compareDoubleExpressionParameters.set("arg2", mathDoubleExpression);

            compareDoubleExpression.put("class", "ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.CompareDoubleExpression");
            compareDoubleExpression.set("parameters", compareDoubleExpressionParameters);

            createTrigger(organization, triggerId, path, parameters, triggerName, priority, objectMapper.writeValueAsString(compareDoubleExpression));
        }
    }

    public void createTriggerIfNotExist(Organization organization, String triggerId, String path, String parameters, String triggerName, TriggerPriority priority, String conf) {
        if (checkNotExist(organization, triggerId)) {
            createTrigger(organization, triggerId, path, parameters, triggerName, priority, conf);
        }
    }

    private boolean checkNotExist(Organization organization, String triggerId) {
        var cachedTrigger = cacheService.getItem("TriggerService::checkNotExist", organization.getId() + "." + triggerId);
        if (cachedTrigger != null) {
            return false;
        }
        Optional<Trigger> optionalTrigger = triggerRepository.findByOrganizationAndTriggerId(organization, triggerId);

        optionalTrigger.ifPresent(trigger -> cacheService.setItem("TriggerService::checkNotExist", organization.getId() + "." + triggerId, trigger));

        return optionalTrigger.isEmpty();
    }

    private void createTrigger(Organization organization, String triggerId, String path, String parameters, String triggerName, TriggerPriority priority, String conf) {
        var resConf = conf
                .replace("__organizationID__", organization.getId().toString());
        var trigger = Trigger.builder()
                .triggerId(triggerId)
                .organization(organization)
                .name(triggerName.formatted(path + (parameters.equals("{}") ? "" : " " + parameters)))
                .priority(priority)
                .conf(resConf)
                .muted(false)
                .enabled(true)
                .description("")
                .lastStatus(TriggerStatus.UNCHECKED)
                .suppressedScore(0)
                .lastStatusUpdate(Instant.now())
                .build();
        var persistentTrigger = triggerRepository.save(trigger);
        cacheService.setItem("TriggerService::checkNotExist", organization.getId() + "." + triggerId, persistentTrigger);
    }

    public void cronCheckTriggers() {
        var triggers = triggerRepository.findAll();
        log.debug("TriggerService::cronCheckTriggers start. Found " + triggers.size() + " triggers");
        triggers.stream()
                .filter(Trigger::isEnabled)
                .forEach(trigger -> {
                    var curStatus = trigger.getLastStatus();
                    var newStatus = TriggerStatus.FAILED;
                    try {
                        newStatus = trigger.checkTrigger() ? TriggerStatus.OK : TriggerStatus.ERROR;
                    } catch (Exception e) {
                        log.warn("TriggerService::cronCheckTriggers error trigger checking", e);
                    }
                    if (curStatus != newStatus) {
                        if (newStatus == TriggerStatus.OK || newStatus == TriggerStatus.ERROR) {
                            var alert = Alert.builder()
                                    .triggerStatus(newStatus)
                                    .alertTimestamp(Instant.now())
                                    .trigger(trigger)
                                    .organization(trigger.getOrganization())
                                    .build();
                            alertRepository.save(alert);
                            alertService.sendAlert(alert);
                        }
                        trigger.setLastStatus(newStatus);
                        trigger.setLastStatusUpdate(Instant.now());
                        trigger.setSuppressedScore(trigger.getSuppressedScore() + 1);
                        triggerRepository.save(trigger);
                    }
                });
    }

    public void createTriggerIfNotExistCheckMetricsRatio(
            Organization organization,
            String triggerId,
            String triggerName,
            String metric1,
            String parameters1,
            String metric2,
            String parameters2,
            TriggerPriority triggerPriority,
            double coefficient
    ) throws JsonProcessingException {
        if (checkNotExist(organization, triggerId)) {
            var objectMapper = new ObjectMapper();

            var parameterGroup1 = parameterGroupRepository.getOrCreateParameterGroup(organization, metric1, parameters1);
            var parameterGroup2 = parameterGroupRepository.getOrCreateParameterGroup(organization, metric2, parameters2);

            var compareDoubleExpression = objectMapper.createObjectNode();
            var compareDoubleExpressionParameters = objectMapper.createObjectNode();

            var constantDoubleExpression = objectMapper.createObjectNode();
            var constantDoubleExpressionParameters = objectMapper.createObjectNode();

            var readValuesOfMetricExpressionParameters1 = objectMapper.createObjectNode();
            var readValuesOfMetricExpression1 = objectMapper.createObjectNode();

            var readValuesOfMetricExpressionParameters2 = objectMapper.createObjectNode();
            var readValuesOfMetricExpression2 = objectMapper.createObjectNode();

            var mathDoubleExpressionParameters = objectMapper.createObjectNode();
            var mathDoubleExpression = objectMapper.createObjectNode();

            constantDoubleExpressionParameters.put("value", coefficient);
            constantDoubleExpression.put("class", "ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression");
            constantDoubleExpression.set("parameters", constantDoubleExpressionParameters);

            readValuesOfMetricExpressionParameters1.put("parameterGroup", String.valueOf(parameterGroup1.getId()));
            readValuesOfMetricExpressionParameters1.put("collectorClass", "ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.LastItemTimestampCollector");
            readValuesOfMetricExpressionParameters1.put("organizationID", "__organizationID__");
            readValuesOfMetricExpressionParameters1.put("beginDiff", 2592000L);
            readValuesOfMetricExpressionParameters1.put("endDiff", 0L);

            readValuesOfMetricExpression1.put("class", "ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ReadValuesOfMetricExpression");
            readValuesOfMetricExpression1.set("parameters", readValuesOfMetricExpressionParameters1);

            readValuesOfMetricExpressionParameters2.put("parameterGroup", String.valueOf(parameterGroup2.getId()));
            readValuesOfMetricExpressionParameters2.put("collectorClass", "ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.LastItemTimestampCollector");
            readValuesOfMetricExpressionParameters2.put("organizationID", "__organizationID__");
            readValuesOfMetricExpressionParameters2.put("beginDiff", 2592000L);
            readValuesOfMetricExpressionParameters2.put("endDiff", 0L);

            readValuesOfMetricExpression2.put("class", "ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ReadValuesOfMetricExpression");
            readValuesOfMetricExpression2.set("parameters", readValuesOfMetricExpressionParameters2);

            mathDoubleExpressionParameters.put("operation", "/");
            mathDoubleExpressionParameters.set("arg1", readValuesOfMetricExpression1);
            mathDoubleExpressionParameters.set("arg2", readValuesOfMetricExpression2);

            mathDoubleExpression.put("class", "ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.MathDoubleExpression");
            mathDoubleExpression.set("parameters", mathDoubleExpressionParameters);

            compareDoubleExpressionParameters.put("operation", "<");
            compareDoubleExpressionParameters.set("arg1", constantDoubleExpression);
            compareDoubleExpressionParameters.set("arg2", mathDoubleExpression);

            compareDoubleExpression.put("class", "ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.CompareDoubleExpression");
            compareDoubleExpression.set("parameters", compareDoubleExpressionParameters);

            createTrigger(organization, triggerId, metric1, parameters1, triggerName, triggerPriority, objectMapper.writeValueAsString(compareDoubleExpression));
        }

    }
}
