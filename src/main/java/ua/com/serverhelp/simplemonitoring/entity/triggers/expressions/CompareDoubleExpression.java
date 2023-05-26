package ua.com.serverhelp.simplemonitoring.entity.triggers.expressions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class CompareDoubleExpression implements Expression<Boolean> {
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
    public Boolean getValue() throws ExpressionException {
        if (operation == null)
            throw new ExpressionException("Expression not initialized. Operation is null", new Exception());
        boolean res;
        Double arg1val = arg1.getValue();
        Double arg2val = arg2.getValue();

        res = switch (operation) {
            case "<" -> arg1val < arg2val;
            case ">" -> arg1val > arg2val;
            case "<=" -> arg1val <= arg2val;
            case ">=" -> arg1val >= arg2val;
            case "==" -> Objects.equals(arg1val, arg2val);
            case "!=" -> !Objects.equals(arg1val, arg2val);
            default -> throw new ExpressionException("Operation is invalid", new Exception());
        };
        log.debug("CompareDoubleExpression getValue arg1=" + arg1val + " arg2=" + arg2val + " operation=" + operation + " res=" + res);

        return res;
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
