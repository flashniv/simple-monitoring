package ua.com.serverhelp.simplemonitoring.service;

import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.FileManagementService;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.Collector;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

class FileManagementServiceTest extends AbstractTest {
    @Autowired
    private FileManagementService fileManagementService;
    @Value("${metric-storage.metrics-directory}")
    private String dirPath;

    @AfterEach
    void tearDown2() throws Exception {
        File dir = new File(dirPath);
        deleteDirectory(dir);
    }

    @Test
    void writeDataItem() throws Exception {
        var uuid = UUID.randomUUID().toString();
        var parameterGroupId = 1L;
        var filepath = getPath(uuid, parameterGroupId) + getPeriod();

        fileManagementService.writeDataItem(uuid, 1L, DataItem.builder()
                .timestamp(Instant.now())
                .value(1.333)
                .build());

        File file = new File(filepath);
        Assertions.assertTrue(file.exists());
        Assertions.assertTrue(file.isFile());
        Assertions.assertEquals(16L, file.length());
    }

    @Test
    void readMetricWithHook() throws Exception {
        var uuid = UUID.randomUUID().toString();
        var parameterGroupId = 1L;
        var count = new AtomicInteger(0);

        var dataItems = Instancio.ofList(DataItem.class)
                .size(20)
                .set(Select.field(DataItem::getOrganization), null)
                .set(Select.field(DataItem::getPath), null)
                .set(Select.field(DataItem::getParameters), null)
                .generate(Select.field(DataItem::getTimestamp), gen -> gen.temporal().instant().range(Instant.now().minus(2, ChronoUnit.DAYS), Instant.now()))
                .generate(Select.field(DataItem::getValue), gen -> gen.doubles().range(-1000.0, 1000.0))
                .create();
        dataItems.forEach(dataItem -> {
            try {
                fileManagementService.writeDataItem(uuid, parameterGroupId, dataItem);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        fileManagementService.readMetricWithHook(uuid, parameterGroupId, dataItem -> {
            count.incrementAndGet();
        });
        Assertions.assertEquals(20, count.get());
    }

    @Test
    void readMetricWithHookException() {
        var uuid = UUID.randomUUID().toString();
        var parameterGroupId = 1L;

        Assertions.assertThrows(Exception.class, () -> fileManagementService.readMetricWithHook(uuid, parameterGroupId, dataItem -> {
        }));
    }

    @Test
    void readMetricAllItemsCollector() throws Exception {
        var uuid = UUID.randomUUID().toString();
        var parameterGroupId = 1L;

        var dataItems = Instancio.ofList(DataItem.class)
                .size(20)
                .set(Select.field(DataItem::getOrganization), null)
                .set(Select.field(DataItem::getPath), null)
                .set(Select.field(DataItem::getParameters), null)
                .generate(Select.field(DataItem::getTimestamp), gen -> gen.temporal().instant().range(Instant.now().minus(2, ChronoUnit.DAYS), Instant.now()))
                .generate(Select.field(DataItem::getValue), gen -> gen.doubles().range(-1000.0, 1000.0))
                .create();
        dataItems.forEach(dataItem -> {
            try {
                fileManagementService.writeDataItem(uuid, parameterGroupId, dataItem);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var metric = fileManagementService.readMetric(uuid, parameterGroupId, Instant.now().minus(1, ChronoUnit.DAYS), Instant.now(), Collector.allItemsCollector());
        metric.orElseThrow().forEach(dataItem -> {
            var duration = Duration.between(dataItem.getTimestamp(), Instant.now());
            Assertions.assertTrue(duration.toHours() < 24);
        });
    }

    @Test
    void readMetricLastItemValueCollector() throws Exception {
        var uuid = UUID.randomUUID().toString();
        var parameterGroupId = 1L;

        var dataItems = Instancio.ofList(DataItem.class)
                .size(20)
                .set(Select.field(DataItem::getOrganization), null)
                .set(Select.field(DataItem::getPath), null)
                .set(Select.field(DataItem::getParameters), null)
                .generate(Select.field(DataItem::getTimestamp), gen -> gen.temporal().instant().range(Instant.now().minus(2, ChronoUnit.DAYS), Instant.now()))
                .generate(Select.field(DataItem::getValue), gen -> gen.doubles().range(-1000.0, 1000.0))
                .create();
        dataItems.forEach(dataItem -> {
            try {
                fileManagementService.writeDataItem(uuid, parameterGroupId, dataItem);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        var optionalMetric = fileManagementService.readMetric(uuid, parameterGroupId, Instant.now().minus(1, ChronoUnit.DAYS), Instant.now(), Collector.lastItemValueCollector());
        Assertions.assertTrue(optionalMetric.isPresent());
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

    private String getPeriod() {
        LocalDate now = LocalDate.now();
        return now.getYear() + "_" + now.getMonthValue() + "_" + now.getDayOfMonth();
    }

    private String getPath(String orgId, Long parameterGroupId) {
        return dirPath +
                File.separatorChar +
                orgId +
                File.separatorChar +
                (parameterGroupId > 9 ? String.valueOf(parameterGroupId) : "0" + parameterGroupId).substring(0, 2) +
                File.separatorChar +
                parameterGroupId +
                File.separatorChar;
    }
}