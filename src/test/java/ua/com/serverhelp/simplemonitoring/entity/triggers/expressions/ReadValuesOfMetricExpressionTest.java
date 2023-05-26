package ua.com.serverhelp.simplemonitoring.entity.triggers.expressions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ua.com.serverhelp.simplemonitoring.AbstractTest;
import ua.com.serverhelp.simplemonitoring.entity.parametergroup.DataItem;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.FileManagementService;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.AllItemsCollector;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

class ReadValuesOfMetricExpressionTest extends AbstractTest {
    @Autowired
    private FileManagementService fileManagementService;

    @Test
    void getJSON() throws ExpressionException {
        var expression = ReadValuesOfMetricExpression.builder()
                .organizationId(UUID.randomUUID())
                .parameterGroup(1L)
                .beginDiff(60000L)
                .endDiff(0L)
                .collectorClass(AllItemsCollector.class.getName())
                .build();
        var json = expression.getJSON();
        Assertions.assertEquals("{\"class\":\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ReadValuesOfMetricExpression\",\"parameters\":{\"parameterGroup\":1,\"collectorClass\":\"ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.AllItemsCollector\",\"organizationID\":\"__organizationID__\",\"beginDiff\":60000,\"endDiff\":0}}", json);
    }

    @Test
    void initializeWithAllItemsCollector() throws Exception {
        var orgId = UUID.randomUUID().toString();

        fileManagementService.writeDataItem(orgId, 1L, DataItem.builder()
                .timestamp(Instant.now())
                .value(10.0)
                .build());

        var params = "{\"parameterGroup\":1,\"collectorClass\":\"ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.AllItemsCollector\",\"organizationID\":\"__organizationID__\",\"beginDiff\":60000,\"endDiff\":0}}".replaceAll("__organizationID__", orgId);
        var expression = new ReadValuesOfMetricExpression<>();
        expression.initialize(params);
        var dataItems = (List<DataItem>) expression.getValue();
        Assertions.assertEquals(1, dataItems.size());
        var dataItem = dataItems.get(0);
        Assertions.assertEquals(10.0, dataItem.getValue());
    }

    @Test
    void initializeWithLastItemCollector() throws Exception {
        var orgId = UUID.randomUUID().toString();

        fileManagementService.writeDataItem(orgId, 1L, DataItem.builder()
                .timestamp(Instant.now())
                .value(10.0)
                .build());

        var params = "{\"parameterGroup\":1,\"collectorClass\":\"ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.LastItemValueCollector\",\"organizationID\":\"__organizationID__\",\"beginDiff\":60000,\"endDiff\":0}}".replaceAll("__organizationID__", orgId);
        var expression = new ReadValuesOfMetricExpression<>();
        expression.initialize(params);
        var dataItemValue = (Double) expression.getValue();
        Assertions.assertEquals(10.0, dataItemValue);
    }
}