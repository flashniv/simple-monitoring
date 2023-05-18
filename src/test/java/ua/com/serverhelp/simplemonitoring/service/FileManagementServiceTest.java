package ua.com.serverhelp.simplemonitoring.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;

import java.io.File;
import java.time.Instant;
import java.util.UUID;

class FileManagementServiceTest extends AbstractTest {
    @Autowired
    private FileManagementService fileManagementService;
    @Value("${metric-storage.metrics-directory}")
    private String dirPath;


    @AfterEach
    void tearDown2() throws Exception{
        File dir=new File(dirPath);
        deleteDirectory(dir);
    }

    @Test
    void writeDataItem() {
        String uuid=UUID.randomUUID().toString();
        fileManagementService.writeDataItem(uuid, 1L, DataItem.builder()
                .timestamp(Instant.now())
                .value(1.333)
                .build());

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