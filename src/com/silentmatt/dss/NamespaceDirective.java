package com.silentmatt.dss;

/**
 *
 * @author matt
 */
public class NamespaceDirective extends ExpressionDirective {
    private String prefix;

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
        return getName() + " " + (prefix != null ? (prefix + " ") : "") + getExpression().toString();
    }
}
