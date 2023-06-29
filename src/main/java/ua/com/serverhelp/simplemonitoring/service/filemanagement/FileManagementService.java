package ua.com.serverhelp.simplemonitoring.service.filemanagement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.repository.MetricRepository;
import ua.com.serverhelp.simplemonitoring.repository.OrganizationRepository;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.Collector;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileManagementService {
    private final OrganizationRepository organizationRepository;
    private final MetricRepository metricRepository;
    private final ParameterGroupRepository parameterGroupRepository;

    @Value("${metric-storage.metrics-directory}")
    private String dirPath;
    @Value("${metric-storage.pool-deep-days}")
    private int poolDeepDays;

    public void writeDataItem(String orgId, Long parameterGroupId, DataItem dataItem) throws Exception {
        String path = getPath(orgId, parameterGroupId) + getPeriod();
        File dataFile = new File(path);
        //noinspection ResultOfMethodCallIgnored
        dataFile.getParentFile().mkdirs();
        if (dataFile.createNewFile()) {
            log.info("FileDriver::writeMetric file " + path + " created for metric " + parameterGroupId);
        }
        FileOutputStream fos = new FileOutputStream(dataFile, true);
        DataOutputStream dos = new DataOutputStream(fos);

        dos.writeLong(dataItem.getTimestamp().getEpochSecond());
        dos.writeDouble(dataItem.getValue());

        dos.close();
        log.debug("FileDriver::writeMetric Metric " + path + " was write");
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

    public <R> Optional<R> readMetric(String orgId, Long parameterGroupId, Instant begin, Instant end, Collector<R> collector) throws Exception {
        log.debug("FileManagementService::readMetric path=" + orgId + " params=" + parameterGroupId);

        String path = getPath(orgId, parameterGroupId);
        File[] files = collector.getFilteredFiles(Objects.requireNonNull(new File(path).listFiles()), begin, end);

        for (File file : files) {
            log.debug("FileManagementService::readMetricWithHook file=" + file.getAbsolutePath());

            DataInputStream dis = new DataInputStream(new FileInputStream(file));

            while (dis.available() > 0) {
                long timestamp = dis.readLong();
                double value = dis.readDouble();
                var dataElement = DataItem.builder()
                        .timestamp(Instant.ofEpochSecond(timestamp))
                        .value(value)
                        .build();
                if (dataElement.getTimestamp().isAfter(begin) && dataElement.getTimestamp().isBefore(end)) {
                    collector.processItem(dataElement);
                }
            }
            dis.close();
        }

        return collector.getResult();
    }

    private String getPeriod() {
        LocalDate now = LocalDate.now();
        return now.getYear() + "_" + now.getMonthValue() + "_" + now.getDayOfMonth();
    }

    private List<File> getFilesRecursive(File file) {
        List<File> res = new ArrayList<>();
        if (file.isDirectory()) {
            for (File file1 : Objects.requireNonNull(file.listFiles())) {
                res.addAll(getFilesRecursive(file1));
            }
        } else {
            res.add(file);
        }
        return res;
    }

    public void clearMetricDir() {
        var organizations = organizationRepository.findAll();
        File metricDir = new File(dirPath);
        Arrays.stream(Objects.requireNonNull(metricDir.listFiles())).forEach(file -> {
            if (organizations.stream().noneMatch(organization -> organization.getId().toString().equals(file.getName()))) {
                log.info("FileManagementService::clearMetricDir delete not exist organization " + file.getName());
                FileSystemUtils.deleteRecursively(file);
            }
        });
        organizations.forEach(organization -> {
            var metrics = metricRepository.findAllByOrganization(organization);
            var allParameterGroups = new ArrayList<ParameterGroup>();
            metrics.forEach(metric -> {
                allParameterGroups.addAll(parameterGroupRepository.findAllByMetric(metric));
            });
            allParameterGroups.forEach(parameterGroup -> {
                var pgDir = new File(dirPath + File.separatorChar + organization.getId() + File.separatorChar + parameterGroup.getId().toString().substring(0, 2) + File.separatorChar + parameterGroup.getId());
                if (pgDir.exists()) {
                    var partitions = pgDir.listFiles();
                    if (partitions != null) {
                        Arrays.stream(partitions).forEach(file -> {
                            var fileNameParts = file.getName().split("_");
                            var timestamp = Instant.parse(fileNameParts[0] + "-" + (fileNameParts[1].length() == 1 ? "0" + fileNameParts[1] : fileNameParts[1]) + "-" + (fileNameParts[2].length() == 1 ? "0" + fileNameParts[2] : fileNameParts[2]) + "T00:00:00.00Z");
                            var duration = Duration.between(timestamp, Instant.now());
                            if (duration.toDays() > poolDeepDays) {
                                log.info("FileManagementService::clearMetricDir delete too old partition " + organization.getId() + " " + parameterGroup.getId() + " " + file.getName());
                                file.delete();
                            }
                        });
                    }
                }
            });
        });
    }

}
