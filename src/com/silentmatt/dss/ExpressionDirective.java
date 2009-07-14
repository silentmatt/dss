package com.silentmatt.dss;

/**
 *
 * @author matt
 */
public abstract class ExpressionDirective implements Directive {
    private Expression expression;

    public ExpressionDirective(Expression expression) {
        setExpression(expression);
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return getName() + " " + getExpression() + ";";
    }

    public String toString(String start) {
        return start + toString();
    }

    public String toCompactString() {
        return getName() + " " + getExpression().toCompactString() + ";";
    }
}
