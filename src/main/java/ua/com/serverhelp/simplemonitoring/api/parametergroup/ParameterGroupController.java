package ua.com.serverhelp.simplemonitoring.api.parametergroup;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.ParameterGroup;
import ua.com.serverhelp.simplemonitoring.repository.UserRepository;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.FileManagementService;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.Collector;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ParameterGroupController {
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private final FileManagementService fileManagementService;

    @SchemaMapping(typeName = "ParameterGroup", field = "dataItems")
    public List<DataItem> dataItems(ParameterGroup parameterGroup) throws Exception {
        Optional<List<DataItem>> optionalDataItems=fileManagementService.readMetric(
                parameterGroup.getMetric().getOrganization().getId().toString(),
                parameterGroup.getId(),
                Instant.now().minus(1, ChronoUnit.DAYS),
                Instant.now(),
                Collector.allItemsCollector()
        );
        return optionalDataItems.orElse(List.of());
    }
}
