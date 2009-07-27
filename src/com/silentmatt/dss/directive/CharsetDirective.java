package com.silentmatt.dss.directive;

import com.silentmatt.dss.*;
import com.silentmatt.dss.term.Term;

/**
 *
 * @author Matthew Crumley
 */
public class CharsetDirective extends ExpressionDirective {
    public CharsetDirective(Term charset) {
        super(new Expression());
        getExpression().getTerms().add(charset);
    }

    public String getName() {
        return "@charset";
    }

    public DirectiveType getType() {
        return DirectiveType.Charset;
    }
}
