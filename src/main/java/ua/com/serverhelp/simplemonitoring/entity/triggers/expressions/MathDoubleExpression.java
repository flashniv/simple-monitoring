package ua.com.serverhelp.simplemonitoring.entity.triggers.expressions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.InvocationTargetException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MathDoubleExpression implements Expression<Double> {
    private Expression<Double> arg1;
    private Expression<Double> arg2;
    private String operation;

    @Override
    public String getJSON() throws ExpressionException {
        if (operation == null) {
            throw new ExpressionException("Expression not initialized");
        }
        try {
            var objectMapper = new ObjectMapper();
            ObjectNode res = objectMapper.createObjectNode();
            ObjectNode params = objectMapper.createObjectNode();

            params.put("arg1", arg1.getJSON());
            params.put("arg2", arg2.getJSON());
            params.put("operation", operation);

            res.put("class", this.getClass().getName());
            res.set("parameters", params);

            return objectMapper.writeValueAsString(res);
        } catch (JsonProcessingException e) {
            throw new ExpressionException("Error serialize to JSON ", e);
        }
    }

    @Override
    public Double getValue() throws ExpressionException {
        if (operation == null)
            throw new ExpressionException("Expression not initialized. Operation is null", new Exception());
        return switch (operation) {
            case "+" -> arg1.getValue() + arg2.getValue();
            case "-" -> arg1.getValue() - arg2.getValue();
            case "*" -> arg1.getValue() * arg2.getValue();
            case "/" -> arg1.getValue() / arg2.getValue();
            case "^" -> Math.pow(arg1.getValue(), arg2.getValue());
            case "max" -> Math.max(arg1.getValue(), arg2.getValue());
            case "min" -> Math.min(arg1.getValue(), arg2.getValue());
            default -> throw new ExpressionException("Operation is invalid", new Exception());
        };
    }

    @Override
    public void initialize(String parametersJson) throws ExpressionException {
        try {
            var objectMapper = new ObjectMapper();
            JsonNode parameters = objectMapper.readTree(parametersJson);
            JsonNode arg1Json = objectMapper.readTree(parameters.get("arg1").asText());
            JsonNode arg2Json = objectMapper.readTree(parameters.get("arg2").asText());
            operation = parameters.get("operation").asText();

            Class<?> arg1Class = Class.forName(arg1Json.get("class").asText());
            Expression<Double> arg1 = (Expression<Double>) arg1Class.getConstructor().newInstance();
            arg1.initialize(objectMapper.writeValueAsString(arg1Json.get("parameters")));
            setArg1(arg1);

            Class<?> arg2Class = Class.forName(arg2Json.get("class").asText());
            Expression<Double> arg2 = (Expression<Double>) arg2Class.getConstructor().newInstance();
            arg2.initialize(objectMapper.writeValueAsString(arg2Json.get("parameters")));
            setArg2(arg2);
        } catch (JsonProcessingException e) {
            throw new ExpressionException("JSON decode error", e);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new ExpressionException("Class load error", e);
        }
    }
}
