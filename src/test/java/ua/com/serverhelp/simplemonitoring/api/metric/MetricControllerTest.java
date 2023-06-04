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
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;

@Slf4j
@AutoConfigureGraphQlTester
class MetricControllerTest extends AbstractTest {
    @Autowired
    private GraphQlTester tester;
    private Organization organization;

    @BeforeEach
    void setUp2() {
        registerTestUsers();
        organization=createOrganization().get(0);
    }

    @Test
    @WithMockUser("admin@mail.com")
    void metricsByAdmin() {
        var document = """
                {
                    metrics(orgId:"__orgId__"){
                        id
                        name
                    }
                }
                """.replace("__orgId__", organization.getId().toString());
        var metrics = tester
                .document(document)
                .execute()
                .path("metrics")
                .entityList(Metric.class)
                .get();
        Assertions.assertEquals(10, metrics.size());
    }

    @Test
    @WithMockUser("manager@mail.com")
    void metricsByManager() {
        var document = """
                {
                    metrics(orgId:"__orgId__"){
                        id
                        name
                    }
                }
                """.replace("__orgId__", organization.getId().toString());
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
                    metrics(orgId:"__orgId__"){
                        id
                        name
                        parameterGroups{
                            id
                        }
                    }
                }
                """.replace("__orgId__", organization.getId().toString());
        var metrics = tester
                .document(document)
                .execute()
                .path("metrics")
                .entityList(Metric.class)
                .get();
        Assertions.assertEquals(10, metrics.size());
    }
}