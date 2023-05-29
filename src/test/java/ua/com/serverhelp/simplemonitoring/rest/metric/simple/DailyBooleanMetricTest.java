package ua.com.serverhelp.simplemonitoring.rest.metric.simple;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.entity.triggers.Trigger;
import ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ExpressionException;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;
import ua.com.serverhelp.simplemonitoring.service.DataItemsService;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.FileManagementService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

class DailyBooleanMetricTest extends AbstractTest {
    @MockBean
    private DataItemsService dataItemsService;
    @MockBean
    private FileManagementService fileManagementService;
    @MockBean
    private ParameterGroupRepository parameterGroupRepository;

    @BeforeEach
    void setUp2() throws Exception {
        Mockito.when(parameterGroupRepository.getOrCreateParameterGroup(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(
                        ParameterGroup.builder()
                                .id(1L)
                                .build()
                );
        Mockito.when(fileManagementService.readMetric(Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(new ExpressionException("Mock"))
                .thenThrow(new ExpressionException("Mock"))
                .thenReturn(Optional.of(0.0))
                .thenReturn(Optional.of((double)Instant.now().minus(10, ChronoUnit.DAYS).getEpochSecond()))
                .thenReturn(Optional.of(1.0))
                .thenReturn(Optional.of((double)Instant.now().minus(100, ChronoUnit.MINUTES).getEpochSecond()));
    }

    @Test
    void getAddEvent() throws Exception {
        registerTestUsers();
        var organizations = createOrganization();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/metric/simple/dailyboolean/")
                        .header("X-Simple-Token", accessToken.getId())
                        .param("path", "test.stage.db.booleanitem1")
                        .param("triggerName", "Boolean trigger %s receive false")
                        .param("value", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.content().string("Success"));
        //Check trigger
        Optional<Trigger> optionalTrigger = triggerRepository.findByOrganizationAndTriggerId(organizations.get(0), "test.stage.db.booleanitem1{}.boolean");
        Assertions.assertTrue(optionalTrigger.isPresent());
        var booleanTrigger = optionalTrigger.get();
        //Check daily trigger
        optionalTrigger = triggerRepository.findByOrganizationAndTriggerId(organizations.get(0), "test.stage.db.booleanitem1{}.daily");
        Assertions.assertTrue(optionalTrigger.isPresent());
        var dailyTrigger = optionalTrigger.get();

        Assertions.assertThrows(ExpressionException.class,() -> booleanTrigger.checkTrigger());
        Assertions.assertThrows(ExpressionException.class,() -> dailyTrigger.checkTrigger());

        Assertions.assertFalse(booleanTrigger.checkTrigger());
        Assertions.assertFalse(dailyTrigger.checkTrigger());

        Assertions.assertTrue(booleanTrigger.checkTrigger());
        Assertions.assertTrue(dailyTrigger.checkTrigger());
    }

}