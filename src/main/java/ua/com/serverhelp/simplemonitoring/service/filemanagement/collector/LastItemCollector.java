package ua.com.serverhelp.simplemonitoring.service.filemanagement.collector;

import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;

import java.util.Optional;

public class LastItemCollector implements Collector<DataItem> {
    private DataItem resDataItem;

    @Override
    public void processItem(DataItem dataItem) {
        if (resDataItem == null || resDataItem.getTimestamp().isBefore(dataItem.getTimestamp())) {
            resDataItem = dataItem;
        }
    }

    @Override
    public Optional<DataItem> getResult() {
        if (resDataItem == null) {
            return Optional.empty();
        }
        return Optional.of(resDataItem);
    }
}
