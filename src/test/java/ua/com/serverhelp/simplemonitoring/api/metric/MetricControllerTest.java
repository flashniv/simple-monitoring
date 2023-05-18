package ua.com.serverhelp.simplemonitoring.api.metric;

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
class MetricControllerTest extends AbstractTest {
    @Autowired
    private GraphQlTester tester;

    @BeforeEach
    void setUp() {
        registerTestUsers();
        createOrganization();
    }

    @Test
    @WithMockUser("admin@mail.com")
    void metricsByAdmin() {
        var document = """
                {
                    metrics{
                        id
                        name
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
    }

    @Test
    @WithMockUser("manager@mail.com")
    void metricsByManager() {
        var document = """
                {
                    metrics{
                        id
                        name
                    }
                }
                """;
        var metrics = tester
                .document(document)
                .execute()
                .path("metrics")
                .entityList(Metric.class)
                .get();
        Assertions.assertEquals(10, metrics.size());
    }

    @Test
    @WithMockUser("admin@mail.com")
    void parameterGroups() {
        var document = """
                {
                    metrics{
                        id
                        name
                        parameterGroups{
                            id
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
    }
}