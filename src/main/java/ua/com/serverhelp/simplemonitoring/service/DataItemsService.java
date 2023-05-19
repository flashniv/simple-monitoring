package ua.com.serverhelp.simplemonitoring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.repository.ParameterGroupRepository;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.FileManagementService;

import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
public class DataItemsService {
    private final ParameterGroupRepository parameterGroupRepository;
    private final FileManagementService fileManagementService;
    private final ConcurrentLinkedQueue<DataItem> queue = new ConcurrentLinkedQueue<>();

    public void putDataItem(DataItem dataItem) {
        queue.add(dataItem);
    }

    public void processItems() {
        while (queue.peek() != null) {
            var dataItem = queue.poll();
            var orgId = dataItem.getOrganization().getId();
            var parameterGroupId = parameterGroupRepository.getOrCreateParameterGroup(dataItem.getOrganization(), dataItem.getPath(), dataItem.getParameters()).getId();
            fileManagementService.writeDataItem(orgId.toString(), parameterGroupId, dataItem);
        }
    }
}
