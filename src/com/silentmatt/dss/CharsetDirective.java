package com.silentmatt.dss;

/**
 *
 * @author Matthew Crumley
 */
public class CharsetDirective extends ExpressionDirective {
    public CharsetDirective(Expression charset) {
        super(charset);
    }

    public String getName() {
        return "@charset";
    }

    public DirectiveType getType() {
        return DirectiveType.Charset;
    }
}
