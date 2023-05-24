package ua.com.serverhelp.simplemonitoring.entity.triggers.expressions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConstantDoubleExpressionTest {

    @Test
    void getJSON() throws ExpressionException {
        var expression=new ConstantDoubleExpression(123.456);
        var json=expression.getJSON();
        Assertions.assertEquals("{\"class\":\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression\",\"parameters\":{\"value\":123.456}}", json);
    }

    @Test
    void initialize() throws ExpressionException {
        var expression=new ConstantDoubleExpression();
        Assertions.assertThrows(ExpressionException.class, expression::getJSON);
        Assertions.assertThrows(ExpressionException.class, expression::getValue);
        expression.initialize("{\"class\":\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression\",\"parameters\":{\"value\":123.456}}");
        Assertions.assertEquals(123.456, expression.getValue());
    }
}