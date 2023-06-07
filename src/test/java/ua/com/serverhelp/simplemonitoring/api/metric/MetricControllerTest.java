package ua.com.serverhelp.simplemonitoring.api.metric;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.FileManagementService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Slf4j
@AutoConfigureGraphQlTester
class MetricControllerTest extends AbstractTest {
    @MockBean
    private FileManagementService fileManagementService;
    @Autowired
    private GraphQlTester tester;
    private Organization organization;

    @BeforeEach
    void setUp2() throws Exception {
        registerTestUsers();
        organization = createOrganization().get(0);
        Mockito.when(fileManagementService.readMetric(Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(List.of(DataItem.builder()
                        .value(0.01)
                        .timestamp(Instant.now())
                        .build()
                )));
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

    @Test
    @WithMockUser("admin@mail.com")
    void metric() {
        List<Metric> metrics = metricRepository.findAll();
        Assertions.assertFalse(metrics.isEmpty());
        Metric metric = metrics.get(0);

        var document = """
                {
                    metric(metricId:"__metricId__"){
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
                """.replace("__metricId__", String.valueOf(metric.getId()));
        var result = tester
                .document(document)
                .execute()
                .path("metric")
                .entity(Metric.class)
                .get();
        Assertions.assertEquals(15, result.getParameterGroups().size());
    }
}