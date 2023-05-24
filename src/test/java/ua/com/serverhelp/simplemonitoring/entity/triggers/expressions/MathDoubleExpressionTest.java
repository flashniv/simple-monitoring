package ua.com.serverhelp.simplemonitoring.entity.triggers.expressions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MathDoubleExpressionTest {

    @Test
    void getJSON() throws ExpressionException {
        var oneDigit = new ConstantDoubleExpression(1.0);
        var twoDigit = new ConstantDoubleExpression(2.0);
        var expression = new MathDoubleExpression(oneDigit, twoDigit, "<");
        var json = expression.getJSON();
        Assertions.assertEquals("{\"class\":\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.MathDoubleExpression\",\"parameters\":{\"arg1\":\"{\\\"class\\\":\\\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression\\\",\\\"parameters\\\":{\\\"value\\\":1.0}}\",\"arg2\":\"{\\\"class\\\":\\\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression\\\",\\\"parameters\\\":{\\\"value\\\":2.0}}\",\"operation\":\"<\"}}", json);
    }

    @Test
    void initialize() throws ExpressionException {
        var expression = new MathDoubleExpression();
        Assertions.assertThrows(ExpressionException.class, expression::getJSON);
        Assertions.assertThrows(ExpressionException.class, expression::getValue);
        expression.initialize("{\"arg1\":\"{\\\"class\\\":\\\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression\\\",\\\"parameters\\\":{\\\"value\\\":1.0}}\",\"arg2\":\"{\\\"class\\\":\\\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression\\\",\\\"parameters\\\":{\\\"value\\\":2.0}}\",\"operation\":\"+\"}");
        Assertions.assertEquals(3.0, expression.getValue());

    }
}