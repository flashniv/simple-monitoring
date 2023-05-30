package ua.com.serverhelp.simplemonitoring.service.filemanagement.collector;

import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;

import java.util.Optional;

public class LastItemTimestampCollector implements Collector<Double> {
    private DataItem resDataItem;

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
        return Optional.of((double) resDataItem.getTimestamp().getEpochSecond());
    }
}
