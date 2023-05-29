package ua.com.serverhelp.simplemonitoring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.triggers.Trigger;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerPriority;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerStatus;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;
import ua.com.serverhelp.simplemonitoring.repository.TriggerRepository;

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
    ) {
        if (checkNotExist(organization, triggerId)) {
            var conf = ("{\"class\":\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.CompareDoubleExpression\"," +
                    "\"parameters\":{" +
                    "\"arg1\":\"{\\\"class\\\":\\\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression\\\",\\\"parameters\\\":{\\\"value\\\":__const__}}\"," +
                    "\"arg2\":\"{\\\"class\\\":\\\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ReadValuesOfMetricExpression\\\",\\\"parameters\\\":{\\\"parameterGroup\\\":__parameterGroup__,\\\"collectorClass\\\":\\\"ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.LastItemValueCollector\\\",\\\"organizationID\\\":\\\"__organizationID__\\\",\\\"beginDiff\\\":2592000,\\\"endDiff\\\":0}}\"," +
                    "\"operation\":\"__operation__\"" +
                    "}}")
                            .replace("__const__", String.valueOf(constant))
                            .replace("__operation__", operation);
            createTrigger(organization, triggerId, path, parameters, triggerName, priority, conf);
        }
    }

    public void createTriggerIfNotExist(Organization organization, String triggerId, String path, String parameters, String triggerName, TriggerPriority priority, String conf) {
        if (checkNotExist(organization, triggerId)) {
            createTrigger(organization, triggerId, path, parameters, triggerName, priority, conf);
        }
    }

    private boolean checkNotExist(Organization organization, String triggerId) {
        Optional<Trigger> optionalTrigger = triggerRepository.findByOrganizationAndTriggerId(organization,triggerId);
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
