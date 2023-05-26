package ua.com.serverhelp.simplemonitoring.service.filemanagement;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.Collector;

import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@Slf4j
public class FileManagementService {
    @Value("${metric-storage.metrics-directory}")
    private String dirPath;

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
        readMetricWithHook(orgId, parameterGroupId, dataElement -> {
            if (dataElement.getTimestamp().isAfter(begin) && dataElement.getTimestamp().isBefore(end)) {
                collector.processItem(dataElement);
            }
        });
        return collector.getResult();
    }

    public void readMetricWithHook(String orgId, Long parameterGroupId, Consumer<DataItem> consumer) throws Exception {
        String path = getPath(orgId, parameterGroupId);

        File[] files = Objects.requireNonNull(new File(path).listFiles());

        for (File file : files) {
            DataInputStream dis = new DataInputStream(new FileInputStream(file));

            while (dis.available() > 0) {
                long timestamp = dis.readLong();
                double value = dis.readDouble();
                consumer.accept(DataItem.builder()
                        .timestamp(Instant.ofEpochSecond(timestamp))
                        .value(value)
                        .build());
            }
            dis.close();
        }

        log.debug("FileDriver::readMetric parameter group " + parameterGroupId + " was read.");
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

}
