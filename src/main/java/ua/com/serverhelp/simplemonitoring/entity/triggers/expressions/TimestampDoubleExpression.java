package ua.com.serverhelp.simplemonitoring.entity.triggers.expressions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;

public class TimestampDoubleExpression implements Expression<Double> {
    @Override
    public String getJSON() throws ExpressionException {
        try {
            var objectMapper = new ObjectMapper();
            var res = objectMapper.createObjectNode();
            var params = objectMapper.createObjectNode();

            res.put("class", this.getClass().getName());
            res.set("parameters", params);

            return objectMapper.writeValueAsString(res);
        } catch (JsonProcessingException e) {
            throw new ExpressionException("Error serialize to JSON ", e);
        }
    }

    @Override
    public Double getValue() throws ExpressionException {
        return (double) Instant.now().getEpochSecond();
    }

    @Override
    public void initialize(String parametersJson) throws ExpressionException {
    }
}
