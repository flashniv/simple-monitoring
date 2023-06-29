package ua.com.serverhelp.simplemonitoring.service.filemanagement;

import lombok.extern.slf4j.Slf4j;
import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.Select;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.repository.MetricRepository;
import ua.com.serverhelp.simplemonitoring.repository.OrganizationRepository;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.Collector;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
class FileManagementServiceTest extends AbstractTest {
    @Value("${metric-storage.metrics-directory}")
    private String dirPath;
    @Autowired
    private FileManagementService fileManagementService;
    @MockBean
    private OrganizationRepository organizationRepository;
    @MockBean
    private MetricRepository metricRepository;
    @MockBean
    private ParameterGroupRepository parameterGroupRepository;

    private File forDelete;

    @BeforeEach
    void setUp2() throws IOException {
        var org = Organization.builder()
                .id(UUID.randomUUID())
                .name("test org")
                .build();
        var org1 = Organization.builder()
                .id(UUID.randomUUID())
                .name("test org1")
                .build();
        var org2 = Organization.builder()
                .id(UUID.randomUUID())
                .name("test org2")
                .build();

        var dataItems=Instancio.ofList(DataItem.class)
                .size(20)
                .ignore(Select.field(DataItem::getOrganization))
                .ignore(Select.field(DataItem::getParameters))
                .ignore(Select.field(DataItem::getPath))
                .create();
        var parameterGroups=Instancio.ofList(ParameterGroup.class)
                .size(10)
                .generate(Select.field(ParameterGroup::getId),generators->generators.longSeq().start(1000L))
                .set(Select.field(ParameterGroup::getDataItems), dataItems)
                .ignore(Select.field(ParameterGroup::getMetric))
                .create();

        var metrics = Instancio.ofList(Metric.class)
                .size(20)
                .generate(Select.field(Metric::getId),generators->generators.longSeq().start(1000L))
                .generate(Select.field(Metric::getOrganization), generators-> generators.oneOf(List.of(org,org2,org1)))
                .set(Select.field(Metric::getParameterGroups), parameterGroups)
                .create();

        metrics.forEach(metric -> {
            metric.getParameterGroups().forEach(parameterGroup -> {
                parameterGroup.getDataItems().forEach(dataItem -> {
                    try {
                        fileManagementService.writeDataItem(metric.getOrganization().getId().toString(), parameterGroup.getId(), dataItem);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        });

        forDelete=new File(dirPath+"/"+org.getId()+"/10/1000/1981_1_1");
        forDelete.createNewFile();

        Mockito.when(organizationRepository.findAll()).thenReturn(List.of(org,org1));
        Mockito.when(parameterGroupRepository.findAllByMetric(Mockito.any())).thenReturn(parameterGroups);
        Mockito.when(metricRepository.findAllByOrganization(Mockito.any())).thenReturn(metrics);
    }

    @Test
    void clearMetricDir() {
        fileManagementService.clearMetricDir();
        Assertions.assertFalse(forDelete.exists());
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

//    @Test
//    void readMetricWithHook() throws Exception {
//        var uuid = UUID.randomUUID().toString();
//        var parameterGroupId = 1L;
//        var count = new AtomicInteger(0);
//
//        var dataItems = Instancio.ofList(DataItem.class)
//                .size(20)
//                .set(Select.field(DataItem::getOrganization), null)
//                .set(Select.field(DataItem::getPath), null)
//                .set(Select.field(DataItem::getParameters), null)
//                .generate(Select.field(DataItem::getTimestamp), gen -> gen.temporal().instant().range(Instant.now().minus(2, ChronoUnit.DAYS), Instant.now()))
//                .generate(Select.field(DataItem::getValue), gen -> gen.doubles().range(-1000.0, 1000.0))
//                .create();
//        dataItems.forEach(dataItem -> {
//            try {
//                fileManagementService.writeDataItem(uuid, parameterGroupId, dataItem);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//        fileManagementService.readMetricWithHook(uuid, parameterGroupId, dataItem -> {
//            count.incrementAndGet();
//        });
//        Assertions.assertEquals(20, count.get());
//    }
//
//    @Test
//    void readMetricWithHookException() {
//        var uuid = UUID.randomUUID().toString();
//        var parameterGroupId = 1L;
//
//        Assertions.assertThrows(Exception.class, () -> fileManagementService.readMetricWithHook(uuid, parameterGroupId, dataItem -> {
//        }));
//    }

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

        File newFile = new File("./metrics/" + uuid + "/01/1/2023_6_15");
        newFile.createNewFile();
        File newFile1 = new File("./metrics/" + uuid + "/01/1/2023_6_14");
        newFile1.createNewFile();

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

    @Test
    void readMetricLastItemTimestampCollector() throws Exception {
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

        var optionalMetric = fileManagementService.readMetric(uuid, parameterGroupId, Instant.now().minus(1, ChronoUnit.DAYS), Instant.now(), Collector.lastItemTimestampCollector());
        Assertions.assertTrue(optionalMetric.isPresent());
        log.debug("got timestamp" + Instant.ofEpochSecond(Math.round(optionalMetric.get())));
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