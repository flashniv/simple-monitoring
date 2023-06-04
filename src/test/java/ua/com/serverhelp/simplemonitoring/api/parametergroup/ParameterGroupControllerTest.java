package ua.com.serverhelp.simplemonitoring.api.parametergroup;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@AutoConfigureGraphQlTester
class ParameterGroupControllerTest extends AbstractTest {
    @Autowired
    private GraphQlTester tester;
    @MockBean
    private FileManagementService fileManagementService;
    private Organization organization;

    @BeforeEach
    void setUp2() throws Exception {
        registerTestUsers();
        organization=createOrganization().get(0);
        List<DataItem> dataItems = new ArrayList<>();
        dataItems.add(DataItem.builder()
                .timestamp(Instant.now())
                .value(0.0)
                .build());
        dataItems.add(DataItem.builder()
                .timestamp(Instant.now())
                .value(1.1)
                .build());
        dataItems.add(DataItem.builder()
                .timestamp(Instant.now())
                .value(2.2)
                .build());
        Mockito.when(fileManagementService.readMetric(Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(dataItems));
    }

    @Test
    @WithMockUser("admin@mail.com")
    void parameters() {
        var document = """
                {
                    metrics(orgId:"__orgId__"){
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
                """.replace("__orgId__", organization.getId().toString());
        var metrics = tester
                .document(document)
                .execute()
                .path("metrics")
                .entityList(Metric.class)
                .get();
        Assertions.assertEquals(10, metrics.size());
        var parameterGroups = metrics.get(0).getParameterGroups();

        Assertions.assertEquals(15, parameterGroups.size());
        var parameterGroup = parameterGroups.get(0);
        Assertions.assertNotNull(parameterGroup.getMetric());
        Assertions.assertTrue(parameterGroup.getParameters().contains("{"));
    }

    @Test
    @WithMockUser("admin@mail.com")
    void dataItems() {
        var document = """
                {
                    metrics(orgId:"__orgId__"){
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
                """.replace("__orgId__", organization.getId().toString());
        var metrics = tester
                .document(document)
                .execute()
                .path("metrics")
                .entityList(Metric.class)
                .get();
        Assertions.assertEquals(10, metrics.size());
        var parameterGroups = metrics.get(0).getParameterGroups();

        Assertions.assertEquals(15, parameterGroups.size());
        var parameterGroup = parameterGroups.get(0);
        Assertions.assertTrue(parameterGroup.getParameters().contains("{"));
        var dataItems = parameterGroup.getDataItems();
        Assertions.assertEquals(3, dataItems.size());
        var dataItem = dataItems.get(0);
        Assertions.assertEquals(0.0, dataItem.getValue());
    }
}
