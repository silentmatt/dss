package com.silentmatt.dss.directive;

import com.silentmatt.dss.*;
import com.silentmatt.dss.directive.ExpressionDirective;
import com.silentmatt.dss.directive.DirectiveType;

/**
 *
 * @author Matthew Crumley
 */
public class NamespaceDirective extends ExpressionDirective {
    private String prefix;

    public NamespaceDirective(String prefix, Expression namespace) {
        super(namespace);
        setPrefix(prefix);
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
