package ua.com.serverhelp.simplemonitoring.service.filemanagement.collector;

import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;

import java.io.File;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class AllItemsCollector implements Collector<List<DataItem>> {
    private final List<DataItem> items = new ArrayList<>();

    @Override
    public File[] getFilteredFiles(File[] files, Instant begin, Instant end) {
        return Arrays.stream(files)
                .sorted((o1, o2) -> {
                    String[] o1NameParts = o1.getName().split("_");
                    String[] o2NameParts = o2.getName().split("_");
                    return Math.toIntExact(Instant.parse(o1NameParts[0] + "-" + (o1NameParts[1].length() == 1 ? "0" + o1NameParts[1] : o1NameParts[1]) + "-" + (o1NameParts[2].length() == 1 ? "0" + o1NameParts[2] : o1NameParts[2]) + "T00:00:00.00Z").getEpochSecond() - Instant.parse(o2NameParts[0] + "-" + (o2NameParts[1].length() == 1 ? "0" + o2NameParts[1] : o2NameParts[1]) + "-" + (o2NameParts[2].length() == 1 ? "0" + o2NameParts[2] : o2NameParts[2]) + "T00:00:00.00Z").getEpochSecond());
                })
                .filter(file -> {
                    String[] fileNameParts = file.getName().split("_");
                    Instant fileBegin = Instant.parse(fileNameParts[0] + "-" + (fileNameParts[1].length() == 1 ? "0" + fileNameParts[1] : fileNameParts[1]) + "-" + (fileNameParts[2].length() == 1 ? "0" + fileNameParts[2] : fileNameParts[2]) + "T00:00:00.00Z");
                    Instant fileEnd = Instant.parse(fileNameParts[0] + "-" + (fileNameParts[1].length() == 1 ? "0" + fileNameParts[1] : fileNameParts[1]) + "-" + (fileNameParts[2].length() == 1 ? "0" + fileNameParts[2] : fileNameParts[2]) + "T23:59:59.00Z");
                    if (fileEnd.isBefore(begin)) {
                        return false;
                    } else return !fileBegin.isAfter(end);
                }).toArray(File[]::new);
    }

    @Override
    public void processItem(DataItem dataItem) {
        items.add(dataItem);
    }

    @Override
    public Optional<List<DataItem>> getResult() {
        if (items.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(items.stream()
                .sorted(
                        Comparator.comparingLong(
                                value -> value.getTimestamp().getEpochSecond()
                        )
                ).collect(Collectors.toList())
        );
    }
}
