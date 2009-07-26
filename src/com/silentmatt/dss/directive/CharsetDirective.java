package com.silentmatt.dss.directive;

import com.silentmatt.dss.*;
import com.silentmatt.dss.directive.ExpressionDirective;
import com.silentmatt.dss.directive.DirectiveType;

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
