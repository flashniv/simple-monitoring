package ua.com.serverhelp.simplemonitoring.api.alert;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.alert.Alert;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.triggers.Trigger;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerPriority;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerStatus;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.CompareDoubleExpression;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ExpressionException;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ReadValuesOfMetricExpression;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.LastItemValueCollector;

import java.time.Instant;

@Slf4j
@AutoConfigureGraphQlTester
class AlertControllerTest extends AbstractTest {
    @Autowired
    private GraphQlTester tester;
    private Organization organization;
    private Trigger trigger;

    @BeforeEach
    void setUp2() throws ExpressionException {
        registerTestUsers();
        organization = createOrganization().get(0);
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
                .organization(organization)
                .name("Free disk space less than 15% on exporter.test.scout-db.node{\"device\":\"/dev/md127\",\"fstype\":\"ext4\",\"mountpoint\":\"/mnt/data\"}")
                .lastStatusUpdate(Instant.now())
                .suppressedScore(0)
                .lastStatus(TriggerStatus.OK)
                .description("")
                .muted(false)
                .enabled(true)
                .priority(TriggerPriority.AVERAGE)
                .triggerId("exporter.test.scout-db.node")
                .conf(conf)
                .build();
        this.trigger = triggerRepository.save(trigger);

        var alert = Alert.builder()
                .organization(organization)
                .alertTimestamp(Instant.now())
                .trigger(this.trigger)
                .triggerStatus(TriggerStatus.OK)
                .operationData("")
                .build();
        alertRepository.save(alert);
    }

    @Test
    @WithMockUser("admin@mail.com")
    void alerts() {
        var document = """
                {
                    alerts(orgId:"__orgId__"){
                        id
                        trigger{
                            id
                        }
                        organization{
                            id
                        }
                        triggerStatus
                        alertTimestamp
                        operationData
                    }
                }
                """.replace("__orgId__", organization.getId().toString());
        var result = tester
                .document(document)
                .execute()
                .path("alerts")
                .entityList(Alert.class)
                .get();
        Assertions.assertEquals(1, result.size());
    }
}