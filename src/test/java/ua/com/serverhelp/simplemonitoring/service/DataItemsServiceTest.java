package ua.com.serverhelp.simplemonitoring.service;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;

import java.io.File;
import java.util.List;

class DataItemsServiceTest extends AbstractTest {
    @Autowired
    private DataItemsService dataItemsService;
    @Value("${metric-storage.metrics-directory}")
    private String dirPath;

    @AfterEach
    void tearDown2() throws Exception {
        File dir = new File(dirPath);
        deleteDirectory(dir);
    }

    @BeforeEach
    void setUp() {
        registerTestUsers();
        createOrganization();
        List<Organization> organizations = organizationRepository.findAll();
        Assertions.assertFalse(organizations.isEmpty());
        Organization organization = organizations.get(0);
        var dataItems = Instancio.ofList(DataItem.class)
                .size(30)
                .set(Select.field(DataItem::getOrganization), organization)
                .generate(Select.field(DataItem::getPath), gen -> gen.oneOf("test.organization.item1", "test.organization.item2", "test.organization.item3"))
                .generate(Select.field(DataItem::getParameters), gen -> gen.oneOf("{}", "{\"key\":\"val\"}"))
                .generate(Select.field(DataItem::getTimestamp), gen -> gen.temporal().instant().past())
                .generate(Select.field(DataItem::getValue), gen -> gen.doubles().range(-1000.0, 1000.0))
                .create();
        dataItems.forEach(dataItem -> dataItemsService.putDataItem(dataItem));
    }

    @Test
    void processItems() {
        parameterGroupRepository.deleteAll();
        metricRepository.deleteAll();

        dataItemsService.processItems();

        var metrics=metricRepository.findAll();
        var parameterGroup=parameterGroupRepository.findAll();
        Assertions.assertEquals(3, metrics.size());
        Assertions.assertEquals(6, parameterGroup.size());
    }
    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }

}