package ua.com.serverhelp.simplemonitoring.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.triggers.Trigger;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerPriority;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerStatus;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.FileManagementService;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

class TriggerServiceTest extends AbstractTest {
    @Autowired
    private TriggerService triggerService;
    @MockBean
    private FileManagementService fileManagementService;

    @BeforeEach
    void setUp2() throws Exception {
        Mockito.when(fileManagementService.readMetric(Mockito.anyString(), Mockito.anyLong(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenThrow(Exception.class)
                .thenReturn(Optional.of(0.0))
                .thenReturn(Optional.of(1.0));
    }

    @Test
    void createTriggerIfNotExistCompareToConst() throws JsonProcessingException {
        registerTestUsers();
        var organizations = createOrganization();

        triggerService.createTriggerIfNotExistCompareItemToConst(
                organizations.get(0),
                "triggerID1.boolean",
                "test.item.value",
                "{}",
                "My trigger",
                TriggerPriority.AVERAGE,
                10.0,
                "<"
        );
        Optional<Trigger> optionalTrigger = triggerRepository.findByOrganizationAndTriggerId(organizations.get(0), "triggerID1.boolean");
        Assertions.assertTrue(optionalTrigger.isPresent());
        Trigger trigger = optionalTrigger.get();
        Assertions.assertEquals("My trigger", trigger.getName());
    }

    @Test
    void createTriggerIfNotExistCheckLastTimeItems() throws JsonProcessingException {
        registerTestUsers();
        var organizations = createOrganization();

        triggerService.createTriggerIfNotExistCheckLastTimeItems(
                organizations.get(0),
                "triggerID1.boolean",
                "test.item.value",
                "{}",
                "My trigger",
                TriggerPriority.AVERAGE,
                Duration.of(1, ChronoUnit.DAYS)
        );
        Optional<Trigger> optionalTrigger = triggerRepository.findByOrganizationAndTriggerId(organizations.get(0), "triggerID1.boolean");
        Assertions.assertTrue(optionalTrigger.isPresent());
        Trigger trigger = optionalTrigger.get();
        Assertions.assertEquals("My trigger", trigger.getName());
    }

    @Test
    void createTriggerIfNotExist() {
    }

    @Test
    void cronCheckTriggers() throws JsonProcessingException {
        registerTestUsers();
        var organizations = createOrganization();

        triggerService.createTriggerIfNotExistCompareItemToConst(
                organizations.get(0),
                "triggerID1.boolean",
                "test.item.value",
                "{}",
                "My trigger",
                TriggerPriority.AVERAGE,
                1.0,
                "=="
        );
        Optional<Trigger> optionalTrigger = triggerRepository.findByOrganizationAndTriggerId(organizations.get(0), "triggerID1.boolean");
        Assertions.assertTrue(optionalTrigger.isPresent());
        Trigger trigger = optionalTrigger.get();
        Assertions.assertEquals(TriggerStatus.UNCHECKED, trigger.getLastStatus());

        triggerService.cronCheckTriggers();

        optionalTrigger = triggerRepository.findByOrganizationAndTriggerId(organizations.get(0), "triggerID1.boolean");
        Assertions.assertTrue(optionalTrigger.isPresent());
        trigger = optionalTrigger.get();
        Assertions.assertEquals(TriggerStatus.FAILED, trigger.getLastStatus());

        triggerService.cronCheckTriggers();

        optionalTrigger = triggerRepository.findByOrganizationAndTriggerId(organizations.get(0), "triggerID1.boolean");
        Assertions.assertTrue(optionalTrigger.isPresent());
        trigger = optionalTrigger.get();
        Assertions.assertEquals(TriggerStatus.ERROR, trigger.getLastStatus());

        triggerService.cronCheckTriggers();

        optionalTrigger = triggerRepository.findByOrganizationAndTriggerId(organizations.get(0), "triggerID1.boolean");
        Assertions.assertTrue(optionalTrigger.isPresent());
        trigger = optionalTrigger.get();
        Assertions.assertEquals(TriggerStatus.OK, trigger.getLastStatus());


    }
}