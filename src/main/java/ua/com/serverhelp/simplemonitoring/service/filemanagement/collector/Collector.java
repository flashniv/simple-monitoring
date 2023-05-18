package ua.com.serverhelp.simplemonitoring.service.filemanagement.collector;

import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;

import java.util.List;
import java.util.Optional;

public interface Collector<R> {
    static Collector<List<DataItem>> allItemsCollector() {
        return new AllItemsCollector();
    }

    static Collector<DataItem> lastItemCollector() {
        return new LastItemCollector();
    }

    void processItem(DataItem dataItem);

    Optional<R> getResult();
}
