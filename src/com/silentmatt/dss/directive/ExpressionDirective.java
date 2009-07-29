package com.silentmatt.dss.directive;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Rule;

/**
 *
 * @author Matthew Crumley
 */
public abstract class ExpressionDirective extends Rule {
    private Expression expression;

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

    public String toString(int nesting) {
        return Rule.getIndent(nesting) + toString();
    }

    public String toCssString(int nesting) {
        return toString(nesting);
    }
}
