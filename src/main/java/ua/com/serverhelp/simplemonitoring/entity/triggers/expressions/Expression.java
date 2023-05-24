package ua.com.serverhelp.simplemonitoring.entity.triggers.expressions;

public interface Expression<T> {
    String getJSON() throws ExpressionException;

    /**
     * @return For type Boolean: "true" if trigger status is ok or "false" if failed
     * @throws ExpressionException throw if any of Expressions in recurse can not get value
     */
    T getValue() throws ExpressionException;

    void initialize(String parametersJson) throws ExpressionException;
}
