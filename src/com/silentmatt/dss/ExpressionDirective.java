package com.silentmatt.dss;

/**
 *
 * @author matt
 */
public abstract class ExpressionDirective implements Directive {
    private Expression expression;

    public RuleType getRuleType() {
        return RuleType.Directive;
    }

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

    public String toString(int nesting, boolean compact) {
        String start = "";
        if (!compact) {
            for (int i = 0; i < nesting; i++) {
                start += "\t";
            }
        }
        return start + (compact ? toCompactString() : toString());
    }

    public String toCompactString() {
        return getName() + " " + getExpression().toCompactString() + ";";
    }
}
