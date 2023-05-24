package ua.com.serverhelp.simplemonitoring.entity.triggers.expressions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class ConstantDoubleExpression implements Expression<Double> {
    private final ObjectMapper objectMapper=new ObjectMapper();
    private Double value;

    @Override
    public String getJSON() throws ExpressionException {
        if (value==null){
            throw new ExpressionException("Expression not initialized");
        }
        try {
            ObjectNode res = objectMapper.createObjectNode();
            ObjectNode params = objectMapper.createObjectNode();

            params.put("value", value);

            res.put("class", this.getClass().getName());
            res.set("parameters", params);

            return objectMapper.writeValueAsString(res);
        }catch (JsonProcessingException e){
            throw new ExpressionException("Error serialize to JSON ", e);
        }
    }

    @Override
    public Double getValue() throws ExpressionException {
        if (value==null){
            throw new ExpressionException("Expression not initialized");
        }
        return value;
    }

    @Override
    public void initialize(String parametersJson) throws ExpressionException {
        try {
            JsonNode parameters = objectMapper.readTree(parametersJson).get("parameters");
            value = parameters.get("value").asDouble();
        } catch (JsonProcessingException e) {
            throw new ExpressionException("JSON decode error", e);
        }
    }
}
