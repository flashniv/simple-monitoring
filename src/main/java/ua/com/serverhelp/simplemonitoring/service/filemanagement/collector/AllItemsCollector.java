package ua.com.serverhelp.simplemonitoring.service.filemanagement.collector;

import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AllItemsCollector implements Collector<List<DataItem>> {
    private final List<DataItem> items = new ArrayList<>();

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
