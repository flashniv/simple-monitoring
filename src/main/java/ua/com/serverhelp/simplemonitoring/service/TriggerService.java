package ua.com.serverhelp.simplemonitoring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.triggers.Trigger;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerPriority;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerStatus;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;
import ua.com.serverhelp.simplemonitoring.repository.TriggerRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class TriggerService {
    @Autowired
    private ParameterGroupRepository parameterGroupRepository;
    @Autowired
    private TriggerRepository triggerRepository;

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

//            var conf = ("{\"class\":\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.CompareDoubleExpression\"," +
//                    "\"parameters\":{" +
//                    "\"arg1\":\"{\\\"class\\\":\\\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression\\\",\\\"parameters\\\":{\\\"value\\\":__const__}}\"," +
//                    "\"arg2\":\"{\\\"class\\\":\\\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ReadValuesOfMetricExpression\\\",\\\"parameters\\\":{\\\"parameterGroup\\\":__parameterGroup__,\\\"collectorClass\\\":\\\"ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.LastItemValueCollector\\\",\\\"organizationID\\\":\\\"__organizationID__\\\",\\\"beginDiff\\\":2592000,\\\"endDiff\\\":0}}\"," +
//                    "\"operation\":\"__operation__\"" +
//                    "}}")
//                    .replace("__const__", String.valueOf(constant))
//                    .replace("__operation__", operation);

            var compareDoubleExpression = objectMapper.createObjectNode();
            var compareDoubleExpressionParameters = objectMapper.createObjectNode();
            var constantDoubleExpression = objectMapper.createObjectNode();
            var constantDoubleExpressionParameters = objectMapper.createObjectNode();
            var readValuesOfMetricExpressionParameters = objectMapper.createObjectNode();
            var readValuesOfMetricExpression = objectMapper.createObjectNode();

            constantDoubleExpressionParameters.put("value", constant);
            constantDoubleExpression.put("class", "ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression");
            constantDoubleExpression.set("parameters", constantDoubleExpressionParameters);

            readValuesOfMetricExpressionParameters.put("parameterGroup","__parameterGroup__");
            readValuesOfMetricExpressionParameters.put("collectorClass","ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.LastItemValueCollector");
            readValuesOfMetricExpressionParameters.put("organizationID","__organizationID__");
            readValuesOfMetricExpressionParameters.put("beginDiff",2592000L);
            readValuesOfMetricExpressionParameters.put("endDiff",0L);

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

            readValuesOfMetricExpressionParameters.put("parameterGroup","__parameterGroup__");
            readValuesOfMetricExpressionParameters.put("collectorClass","ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.LastItemTimestampCollector");
            readValuesOfMetricExpressionParameters.put("organizationID","__organizationID__");
            readValuesOfMetricExpressionParameters.put("beginDiff",2592000L);
            readValuesOfMetricExpressionParameters.put("endDiff",0L);

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
        Optional<Trigger> optionalTrigger = triggerRepository.findByOrganizationAndTriggerId(organization, triggerId);
        return optionalTrigger.isEmpty();
    }

    private void createTrigger(Organization organization, String triggerId, String path, String parameters, String triggerName, TriggerPriority priority, String conf) {
        var parameterGroup = parameterGroupRepository.getOrCreateParameterGroup(organization, path, parameters);
        var resConf = conf
                .replace("__organizationID__", organization.getId().toString())
                .replace("__parameterGroup__", String.valueOf(parameterGroup.getId()));
        var trigger = Trigger.builder()
                .triggerId(triggerId)
                .organization(organization)
                .name(triggerName.formatted(path))
                .priority(priority)
                .conf(resConf)
                .muted(false)
                .enabled(true)
                .description("")
                .lastStatus(TriggerStatus.UNCHECKED)
                .suppressedScore(0)
                .lastStatusUpdate(Instant.now())
                .build();
        triggerRepository.save(trigger);
    }

}
