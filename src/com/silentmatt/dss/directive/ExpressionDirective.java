package com.silentmatt.dss.directive;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.declaration.Expression;
import com.silentmatt.dss.rule.Rule;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public abstract class ExpressionDirective extends Rule {
    private final Expression expression;

    public ExpressionDirective(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public String toString(int nesting) {
        return Rule.getIndent(nesting) + toString();
    }
}
