package ua.com.serverhelp.simplemonitoring.entity.triggers.expressions;

public class ExpressionException extends Exception {
    public ExpressionException(String message) {
        super(message);
    }
    public ExpressionException(String message, Exception e) {
        super(message, e);
    }
}
