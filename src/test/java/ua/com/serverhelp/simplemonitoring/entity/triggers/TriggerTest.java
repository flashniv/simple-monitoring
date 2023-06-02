package ua.com.serverhelp.simplemonitoring.entity.triggers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.CompareDoubleExpression;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ReadValuesOfMetricExpression;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.FileManagementService;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.LastItemValueCollector;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

class TriggerTest extends AbstractTest {
    @Autowired
    private FileManagementService fileManagementService;

    @Test
    void checkTrigger() throws Exception {
        var org = Organization.builder()
                .id(UUID.randomUUID())
                .build();
        fileManagementService.writeDataItem(org.getId().toString(), 1L, DataItem.builder()
                .timestamp(Instant.now().minus(3, ChronoUnit.MINUTES))
                .value(10.0)
                .build());
        var expression = CompareDoubleExpression.builder()
                .arg1(ConstantDoubleExpression.builder()
                        .value(5.0)
                        .build())
                .arg2(ReadValuesOfMetricExpression.<Double>builder()
                        .parameterGroup(1L)
                        .beginDiff(300L)
                        .endDiff(0L)
                        .collectorClass(LastItemValueCollector.class.getName())
                        .build())
                .operation("<")
                .build();

        var conf = expression.getJSON();
        var trigger = Trigger.builder()
                .organization(org)
                .conf(conf)
                .build();
        Assertions.assertTrue(trigger.checkTrigger());
    }
}