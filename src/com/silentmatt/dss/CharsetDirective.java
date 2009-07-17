package com.silentmatt.dss;

/**
 *
 * @author matt
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

    public String toString(int nesting, boolean compact) {
        return super.toString(nesting, compact);
    }
}
