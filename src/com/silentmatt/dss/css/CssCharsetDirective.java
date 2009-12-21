package com.silentmatt.dss.css;

/**
 *
 * @author Matthew Crumley
 */
public class CssCharsetDirective extends CssExpressionDirective {
    public CssCharsetDirective(CssTerm charset) {
        super(new CssExpression());
        getExpression().getTerms().add(charset);
    }

    @Override
    public String toString() {
        return "@charset " + getExpression() + ";";
    }
}
