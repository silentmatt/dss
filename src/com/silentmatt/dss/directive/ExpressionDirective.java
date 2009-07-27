package com.silentmatt.dss.directive;

import com.silentmatt.dss.*;

/**
 *
 * @author Matthew Crumley
 */
public abstract class ExpressionDirective extends Directive {
    private Expression expression;

    public RuleType getRuleType() {
        return RuleType.Directive;
    }

    public ExpressionDirective(Expression expression) {
        super();
        this.expression = expression;
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

    public String toString(int nesting) {
        return Rule.getIndent(nesting) + toString();
    }

    public String toCssString(int nesting) {
        return toString(nesting);
    }
}
