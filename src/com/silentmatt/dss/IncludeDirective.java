package com.silentmatt.dss;

/**
 *
 * @author matt
 */
public class IncludeDirective extends ExpressionDirective {
    public IncludeDirective(Expression url) {
        super(url);
    }

    public String getName() {
        return "@include";
    }

    public DirectiveType getType() {
        return DirectiveType.Import;
    }
}
