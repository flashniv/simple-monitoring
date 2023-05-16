package ua.com.serverhelp.simplemonitoring.service;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;

import java.util.List;

class DataItemsServiceTest extends AbstractTest {
    @Autowired
    private DataItemsService dataItemsService;

    @BeforeEach
    void setUp() {
        registerTestUsers();
        createOrganization();
        List<Organization> organizations = organizationRepository.findAll();
        Assertions.assertFalse(organizations.isEmpty());
        Organization organization = organizations.get(0);
        var dataItems = Instancio.ofList(DataItem.class)
                .size(10)
                .generate(Select.field(DataItem::getTimestamp), gen -> gen.temporal().instant().past())
                .generate(Select.field(DataItem::getValue), gen -> gen.doubles().range(-1000.0, 1000.0))
                .create();
        dataItems.forEach(dataItem -> dataItemsService.putDataItem(organization, "test.organization.item", "{\"key\":\"val\"}", dataItem));
    }

    @Test
    void putDataItem() {


    }

    @Test
    void processItems() {
        dataItemsService.processItems();
    }
}