package ua.com.serverhelp.simplemonitoring.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.triggers.Trigger;
import ua.com.serverhelp.simplemonitoring.entity.triggers.TriggerPriority;

import java.util.Optional;

class TriggerServiceTest extends AbstractTest {
    @Autowired
    private TriggerService triggerService;

    @Test
    void createTriggerIfNotExistCompareToConst() {
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
        Optional<Trigger> optionalTrigger=triggerRepository.findByOrganizationAndTriggerId(organizations.get(0),"triggerID1.boolean");
        Assertions.assertTrue(optionalTrigger.isPresent());
        Trigger trigger=optionalTrigger.get();
        Assertions.assertEquals("My trigger", trigger.getName());
    }

    @Test
    void createTriggerIfNotExist() {
    }
}