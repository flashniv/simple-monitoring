package ua.com.serverhelp.simplemonitoring.service;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.repository.MetricRepository;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;

import java.util.concurrent.ConcurrentLinkedQueue;

@Data
@Builder
class FullDataItem {
    private Organization organization;
    private String path;
    private String parameters;
    private DataItem dataItem;
}

@Service
@RequiredArgsConstructor
public class DataItemsService {
    private final ParameterGroupRepository parameterGroupRepository;
    private final MetricRepository metricRepository;
    private final FileManagementService fileManagementService;
    private final ConcurrentLinkedQueue<FullDataItem> queue = new ConcurrentLinkedQueue<>();

    public void putDataItem(Organization organization, String path, String parameters, DataItem dataItem) {
        var fullDataItem = FullDataItem.builder()
                .organization(organization)
                .path(path)
                .parameters(parameters)
                .dataItem(dataItem)
                .build();
        queue.add(fullDataItem);
    }

    public void processItems() {
        while (queue.peek() != null) {
            var fullDataItem = queue.poll();
            var orgId=fullDataItem.getOrganization().getId();
            var parameterGroupId=getOrCreateParameterGroup(fullDataItem.getOrganization(), fullDataItem.getPath(), fullDataItem.getParameters()).getId();
            fileManagementService.writeDataItem(orgId.toString(), parameterGroupId, fullDataItem.getDataItem());
        }
    }

    private ParameterGroup getOrCreateParameterGroup(Organization organization, String path, String parameters) {
        var metric=getOrCreateMetric(organization, path);
        var optionParameterGroup=parameterGroupRepository.findByMetricAndParameters(metric,parameters);
        if (optionParameterGroup.isPresent()){
            return optionParameterGroup.get();
        }
        var parameterGroup=ParameterGroup.builder()
                .metric(metric)
                .parameters(parameters)
                .build();
        return parameterGroupRepository.save(parameterGroup);
    }

    private Metric getOrCreateMetric(Organization organization, String path) {
        var optionMetric = metricRepository.findByOrganizationAndName(organization, path);
        if (optionMetric.isPresent()) {
            return optionMetric.get();
        }
        var metric = Metric.builder()
                .name(path)
                .organization(organization)
                .build();
        return metricRepository.save(metric);
    }
}
