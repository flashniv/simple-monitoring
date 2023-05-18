package ua.com.serverhelp.simplemonitoring.api.parametergroup;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;

@Slf4j
@AutoConfigureGraphQlTester
class ParameterGroupControllerTest extends AbstractTest {
    @Autowired
    private GraphQlTester tester;

    @BeforeEach
    void setUp() {
        registerTestUsers();
        createOrganization();
    }

    @Test
    @WithMockUser("admin@mail.com")
    void parameters() {
        var document = """
                {
                    metrics{
                        id
                        name
                        parameterGroups{
                            id
                            parameters
                            metric{
                                id
                                name
                            }
                        }
                    }
                }
                """;
        var metrics = tester
                .document(document)
                .execute()
                .path("metrics")
                .entityList(Metric.class)
                .get();
        Assertions.assertEquals(20, metrics.size());
        var parameterGroups = metrics.get(0).getParameterGroups();

        Assertions.assertEquals(15, parameterGroups.size());
        var parameterGroup = parameterGroups.get(0);
        Assertions.assertNotNull(parameterGroup.getMetric());
        Assertions.assertTrue(parameterGroup.getParameters().contains("{"));
    }

    @Test
    @WithMockUser("admin@mail.com")
    void dataItems() { //TODO release it
        var document = """
                {
                    metrics{
                        id
                        name
                        parameterGroups{
                            id
                            parameters
                            dataItems{
                                timestamp
                                value
                            }
                        }
                    }
                }
                """;
        var metrics = tester
                .document(document)
                .execute()
                .path("metrics")
                .entityList(Metric.class)
                .get();
    }
}
