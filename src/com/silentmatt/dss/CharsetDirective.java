package com.silentmatt.dss;

/**
 *
 * @author matt
 */
public class CharsetDirective extends ExpressionDirective {
    public String getName() {
        return "@charset";
    }

    public DirectiveType getType() {
        return DirectiveType.Charset;
    }
}
