package com.silentmatt.dss.directive;

import com.silentmatt.dss.*;
import com.silentmatt.dss.directive.Directive;

/**
 *
 * @author Matthew Crumley
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

    public String toString(int nesting) {
        String start = "";
        for (int i = 0; i < nesting; i++) {
            start += "\t";
        }
        return start + (toString());
    }

    public String toCssString(int nesting) {
        return toString(nesting);
    }
}
