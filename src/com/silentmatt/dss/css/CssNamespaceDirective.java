package com.silentmatt.dss.css;

/**
 * @todo Why does this extend ExpressionDirective?
 * @author Matthew Crumley
 */
public class CssNamespaceDirective extends CssExpressionDirective {
    private final String prefix;

    public CssNamespaceDirective(String prefix, CssTerm namespace) {
        super(new CssExpression());
        getExpression().getTerms().add(namespace);
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return "@namespace " + (prefix != null ? (prefix + " ") : "") + getExpression().toString() + ";";
    }
}
