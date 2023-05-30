package ua.com.serverhelp.simplemonitoring.entity.triggers.expressions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TimestampDoubleExpressionTest {

    @Test
    void getJSON() throws ExpressionException {
        var expression = new TimestampDoubleExpression();
        Assertions.assertEquals("{\"class\":\"ua.com.serverhelp.simplemonitoring.entity.triggers.expressions.TimestampDoubleExpression\",\"parameters\":{}}", expression.getJSON());
    }
}