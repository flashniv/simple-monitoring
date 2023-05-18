package ua.com.serverhelp.simplemonitoring.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;

import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class FileManagementService {
    @Value("${metric-storage.metrics-directory}")
    private String dirPath;

    public void writeDataItem(String orgId, Long parameterGroupId, DataItem dataItem) {
        String path = getPath(orgId, parameterGroupId)+getPeriod();
        try {
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
        } catch (Exception e) {
            log.error("Error metric writing " + path, e);
        }
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

    /*public DataElement readLastEventOfMetric(String metricName) throws IOException, ClassNotFoundException, ExpressionException {
        DataElement lastDataElement = new DataElement(0, 0.0);
        AtomicBoolean found = new AtomicBoolean(false);
        readMetricWithHook(metricName, dataElement -> {
            found.set(true);
            if (dataElement.getTimestamp() > lastDataElement.getTimestamp()) {
                lastDataElement.setTimestamp(dataElement.getTimestamp());
                lastDataElement.setValue(dataElement.getValue());
            }
        });
        if (!found.get()) {
            throw new ExpressionException("Metric not have any values", new Exception());
        }
        log.debug("Read element " + lastDataElement);
        return lastDataElement;
    }

    public List<DataElement> readMetric(String metricName, Instant begin, Instant end) throws IOException, ClassNotFoundException {
        List<DataElement> dataElements = new ArrayList<>();
        readMetricWithHook(metricName, dataElement -> {
            if (dataElement.getTimestamp() > begin.getEpochSecond() && dataElement.getTimestamp() <= end.getEpochSecond()) {
                dataElements.add(dataElement);
            }
        });
        return dataElements.stream().sorted(Comparator.comparingLong(DataElement::getTimestamp)).collect(Collectors.toList());
    }*/
    public void readMetricWithHook(String orgId, Long parameterGroupId, Consumer<DataItem> consumer) throws Exception {
        String path=getPath(orgId, parameterGroupId);

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

        log.debug("FileDriver::readMetric Metric " + metricName + " was read.");
    }

    private String getPeriod() {
        LocalDate now = LocalDate.now();
        return now.getYear() + "_" + now.getMonthValue() + "_" + now.getDayOfMonth();
    }

    public List<File> getAllFiles() throws IOException {
        return getFilesRecursive(new File(dirPath));
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
