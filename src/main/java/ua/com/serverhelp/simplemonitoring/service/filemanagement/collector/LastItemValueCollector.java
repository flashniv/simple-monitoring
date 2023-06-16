package ua.com.serverhelp.simplemonitoring.service.filemanagement.collector;

import lombok.extern.slf4j.Slf4j;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;

import java.io.File;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class LastItemValueCollector implements Collector<Double> {
    private DataItem resDataItem;

    @Override
    public File[] getFilteredFiles(File[] files, Instant begin, Instant end) {
        File[] files1 = Arrays.stream(files).sorted((o1, o2) -> {
            String[] o1NameParts = o1.getName().split("_");
            String[] o2NameParts = o2.getName().split("_");
            return Math.toIntExact(Instant.parse(o1NameParts[0] + "-" + (o1NameParts[1].length()==1?"0"+o1NameParts[1]:o1NameParts[1]) + "-" + (o1NameParts[2].length()==1?"0"+o1NameParts[2]:o1NameParts[2]) + "T00:00:00.00Z").getEpochSecond()-Instant.parse(o2NameParts[0] + "-" + (o2NameParts[1].length()==1?"0"+o2NameParts[1]:o2NameParts[1]) + "-" + (o2NameParts[2].length()==1?"0"+o2NameParts[2]:o2NameParts[2]) + "T00:00:00.00Z").getEpochSecond());
        }).toArray(File[]::new);
        File[] files2=new File[1];
        files2[0]=files1[files1.length-1];

        return files2;
    }

    @Override
    public void processItem(DataItem dataItem) {
        if (resDataItem == null || resDataItem.getTimestamp().isBefore(dataItem.getTimestamp())) {
            resDataItem = dataItem;
        }
    }

    @Override
    public Optional<Double> getResult() {
        if (resDataItem == null) {
            return Optional.empty();
        }
        return Optional.of(resDataItem.getValue());
    }
}
