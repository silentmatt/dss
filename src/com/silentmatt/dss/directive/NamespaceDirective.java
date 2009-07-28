package com.silentmatt.dss.directive;

import com.silentmatt.dss.*;
import com.silentmatt.dss.term.UrlTerm;

/**
 * @todo Why does this extend ExpressionDirective?
 * @author Matthew Crumley
 */
public class NamespaceDirective extends ExpressionDirective {
    private String prefix;

    public NamespaceDirective(String prefix, UrlTerm namespace) {
        super(new Expression());
        getExpression().getTerms().add(namespace);
        this.prefix = prefix;
    }

    public String getName() {
        return "@namespace";
    }

    public DirectiveType getType() {
        return DirectiveType.Namespace;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return getName() + " " + (prefix != null ? (prefix + " ") : "") + getExpression().toString() + ";";
    }
}
