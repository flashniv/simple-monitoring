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
import ua.com.serverhelp.simplemonitoring.entity.triggers.Trigger;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;
import ua.com.serverhelp.simplemonitoring.service.DataItemsService;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

class BooleanMetricRestTest extends AbstractTest {
    @Autowired
    private DataItemsService dataItemsService;
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
        var organizations = createOrganization();

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

        Mockito.verify(parameterGroupRepository, Mockito.times(2)).getOrCreateParameterGroup(Mockito.any(), Mockito.any(), Mockito.any());
        //Check trigger
        Optional<Trigger> optionalTrigger = triggerRepository.findByOrganizationAndTriggerId(organizations.get(0), "test.stage.db.booleanitem1{}.boolean");
        Assertions.assertTrue(optionalTrigger.isPresent());
        Trigger trigger = optionalTrigger.get();
        Assertions.assertTrue(trigger.checkTrigger());

        Thread.sleep(1000);

        //Check false
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/metric/simple/boolean/")
                        .header("X-Simple-Token", accessToken.getId())
                        .param("path", "test.stage.db.booleanitem1")
                        .param("triggerName", "Boolean trigger %s receive false")
                        .param("value", "false")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));

        dataItemsService.processItems();
        Assertions.assertFalse(trigger.checkTrigger());
    }
}