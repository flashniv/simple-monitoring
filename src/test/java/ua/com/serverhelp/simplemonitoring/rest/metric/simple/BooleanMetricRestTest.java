package ua.com.serverhelp.simplemonitoring.rest.metric.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;
import ua.com.serverhelp.simplemonitoring.service.DataItemsService;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.FileManagementService;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;

class BooleanMetricRestTest extends AbstractTest {
    @Autowired
    private DataItemsService dataItemsService;
    @MockBean
    private FileManagementService fileManagementService;
    @MockBean
    private ParameterGroupRepository parameterGroupRepository;

    @BeforeEach
    void setUp2() {
        Mockito.when(parameterGroupRepository.getOrCreateParameterGroup(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(ParameterGroup.builder()
                .id(1L)
                .build());
    }

    @Test
    void getAddEvent() throws Exception {
        registerTestUsers();
        createOrganization();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/metric/simple/boolean/")
                        .header("X-Simple-Token", accessToken.getId())
                        .param("path", "test.stage.db.booleanitem1")
                        .param("triggerName", "Boolean trigger %s receive false")
                        .param("value", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
        Field queueField = dataItemsService.getClass().getDeclaredField("queue");
        queueField.setAccessible(true);
        ConcurrentLinkedQueue<DataItem> queue = (ConcurrentLinkedQueue<DataItem>) queueField.get(dataItemsService);
        Assertions.assertTrue(queue.peek() != null);
        var dataItem = queue.peek();
        Assertions.assertEquals(1.0, dataItem.getValue());
        Assertions.assertEquals("{}", dataItem.getParameters());
        Assertions.assertEquals("test.stage.db.booleanitem1", dataItem.getPath());
        Assertions.assertNotNull(dataItem.getOrganization());

        dataItemsService.processItems();

        Mockito.verify(fileManagementService).writeDataItem(Mockito.anyString(), Mockito.anyLong(), Mockito.any());
        Mockito.verify(parameterGroupRepository).getOrCreateParameterGroup(Mockito.any(), Mockito.any(), Mockito.any());
    }
}