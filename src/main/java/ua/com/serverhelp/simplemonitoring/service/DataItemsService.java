package ua.com.serverhelp.simplemonitoring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemonitoring.entity.metric.Metric;
import ua.com.serverhelp.simplemonitoring.entity.organization.Organization;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.repository.MetricRepository;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.FileManagementService;

import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
public class DataItemsService {
    private final ParameterGroupRepository parameterGroupRepository;
    private final MetricRepository metricRepository;
    private final FileManagementService fileManagementService;
    private final ConcurrentLinkedQueue<DataItem> queue = new ConcurrentLinkedQueue<>();

    public void putDataItem(DataItem dataItem) {
        queue.add(dataItem);
    }

    public void processItems() {
        while (queue.peek() != null) {
            var dataItem = queue.poll();
            var orgId = dataItem.getOrganization().getId();
            var parameterGroupId = getOrCreateParameterGroup(dataItem.getOrganization(), dataItem.getPath(), dataItem.getParameters()).getId();
            fileManagementService.writeDataItem(orgId.toString(), parameterGroupId, dataItem);
        }
    }

    private ParameterGroup getOrCreateParameterGroup(Organization organization, String path, String parameters) {
        var metric = getOrCreateMetric(organization, path);
        var optionParameterGroup = parameterGroupRepository.findByMetricAndParameters(metric, parameters);
        if (optionParameterGroup.isPresent()) {
            return optionParameterGroup.get();
        }
        var parameterGroup = ParameterGroup.builder()
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
