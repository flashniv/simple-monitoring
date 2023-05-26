package ua.com.serverhelp.simplemonitoring.entity.triggers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.CompareDoubleExpression;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ReadValuesOfMetricExpression;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.FileManagementService;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.LastItemValueCollector;

import java.util.Optional;
import java.util.UUID;

class TriggerTest extends AbstractTest {
    @MockBean
    private FileManagementService fileManagementService;

    @BeforeEach
    void setUp2() throws Exception {
        Mockito.doAnswer(invocationOnMock -> Optional.of(10.0))
                .when(fileManagementService).readMetric(Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void checkTrigger() throws Exception {
        var expression = CompareDoubleExpression.builder()
                .arg1(ConstantDoubleExpression.builder()
                        .value(5.0)
                        .build())
                .arg2(ReadValuesOfMetricExpression.<Double>builder()
                        .parameterGroup(1L)
                        .beginDiff(0L)
                        .endDiff(0L)
                        .collectorClass(LastItemValueCollector.class.getName())
                        .build())
                .operation("<")
                .build();

        var conf = expression.getJSON();
        var trigger = Trigger.builder()
                .organization(Organization.builder()
                        .id(UUID.randomUUID())
                        .build())
                .conf(conf)
                .build();
        Assertions.assertTrue(trigger.checkTrigger());
    }
}