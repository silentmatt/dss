package com.silentmatt.dss.css;

/**
 *
 * @author Matthew Crumley
 */
public abstract class CssExpressionDirective extends CssRule {
    private final CssExpression expression;

    public CssExpressionDirective(CssExpression expression) {
        super();
        this.expression = expression;
    }

    public CssExpression getExpression() {
        return expression;
    }

    @Override
    public String toString(int nesting) {
        return CssRule.getIndent(nesting) + toString();
    }

    @Override
    public String toString(boolean compact, int nesting) {
        return toString();
    }
}
