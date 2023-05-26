package ua.com.serverhelp.simplemonitoring.entity.triggers.expressions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.com.serverhelp.simplemonitoring.config.SpringContext;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.FileManagementService;
import ua.com.serverhelp.simplemonitoring.service.filemanagement.collector.Collector;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReadValuesOfMetricExpression<T> implements Expression<T> {
    private UUID organizationId;
    private Long parameterGroup;
    private String collectorClass;
    private long beginDiff;
    private long endDiff;

    @Override
    public String getJSON() throws ExpressionException {
        if (parameterGroup == null) {
            throw new ExpressionException("Expression not initialized");
        }
        try {
            var objectMapper = new ObjectMapper();
            ObjectNode res = objectMapper.createObjectNode();
            ObjectNode params = objectMapper.createObjectNode();

            params.put("parameterGroup", parameterGroup);
            params.put("collectorClass", collectorClass);
            params.put("organizationID", "__organizationID__");
            params.put("beginDiff", beginDiff);
            params.put("endDiff", endDiff);

            res.put("class", this.getClass().getName());
            res.set("parameters", params);

            return objectMapper.writeValueAsString(res);
        } catch (JsonProcessingException e) {
            throw new ExpressionException("Error serialize to JSON ", e);
        }
    }

    @Override
    public T getValue() throws ExpressionException {
        if (parameterGroup == null) {
            throw new ExpressionException("Expression not initialized");
        }
        try {
            var appCtx = SpringContext.getAppContext();

            Class<?> classType = Class.forName(collectorClass);

            Collector<T> collector = (Collector<T>) classType.getConstructor().newInstance();

            var fileManagementService = appCtx.getBean(FileManagementService.class);
            var metricsOptional = fileManagementService.readMetric(
                    organizationId.toString(),
                    parameterGroup,
                    Instant.now().minus(beginDiff, ChronoUnit.SECONDS),
                    Instant.now().minus(endDiff, ChronoUnit.SECONDS),
                    collector
            );

            return metricsOptional.orElseThrow();
        } catch (Exception e) {
            throw new ExpressionException("Read metric error", e);
        }
    }

    @Override
    public void initialize(String parametersJson) throws ExpressionException {
        try {
            var objectMapper = new ObjectMapper();
            var parameters = objectMapper.readTree(parametersJson);
            parameterGroup = parameters.get("parameterGroup").asLong();
            collectorClass = parameters.get("collectorClass").asText();
            organizationId = UUID.fromString(parameters.get("organizationID").asText());
            beginDiff = parameters.get("beginDiff").asLong();
            endDiff = parameters.get("endDiff").asLong();
        } catch (JsonProcessingException e) {
            throw new ExpressionException("JSON decode error", e);
        }
    }
}
