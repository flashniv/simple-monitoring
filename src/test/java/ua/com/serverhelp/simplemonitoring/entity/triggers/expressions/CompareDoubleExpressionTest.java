package ua.com.serverhelp.simplemonitoring.entity.triggers.expressions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CompareDoubleExpressionTest {

    @Test
    void getJSON() throws ExpressionException {
        var oneDigit = new ConstantDoubleExpression(1.0);
        var twoDigit = new ConstantDoubleExpression(2.0);
        var compareExpression = new CompareDoubleExpression(oneDigit, twoDigit, "<");
        var json = compareExpression.getJSON();
        Assertions.assertEquals("{\"class\":\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.CompareDoubleExpression\",\"parameters\":{\"arg1\":\"{\\\"class\\\":\\\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression\\\",\\\"parameters\\\":{\\\"value\\\":1.0}}\",\"arg2\":\"{\\\"class\\\":\\\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression\\\",\\\"parameters\\\":{\\\"value\\\":2.0}}\",\"operation\":\"<\"}}", json);
    }

    @Test
    void initialize() throws ExpressionException {
        var compareExpression=new CompareDoubleExpression();
        Assertions.assertThrows(ExpressionException.class, compareExpression::getJSON);
        Assertions.assertThrows(ExpressionException.class, compareExpression::getValue);
        compareExpression.initialize("{\"arg1\":\"{\\\"class\\\":\\\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression\\\",\\\"parameters\\\":{\\\"value\\\":1.0}}\",\"arg2\":\"{\\\"class\\\":\\\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.ConstantDoubleExpression\\\",\\\"parameters\\\":{\\\"value\\\":2.0}}\",\"operation\":\"<\"}");
        Assertions.assertTrue(compareExpression.getValue());
    }
}