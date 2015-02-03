package com.silentmatt.dss.directive;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.bool.BooleanExpression;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.declaration.Declaration;
import com.silentmatt.dss.declaration.DeclarationList;
import com.silentmatt.dss.declaration.Expression;
import com.silentmatt.dss.evaluator.EvaluationState;
import com.silentmatt.dss.evaluator.Scope;
import com.silentmatt.dss.rule.Rule;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public final class DefineDirective extends DeclarationDirective {
    private final boolean global;
    private final BooleanExpression condition;

    public DefineDirective(DeclarationList declarations, boolean global, BooleanExpression condition) {
        super(declarations);
        this.global = global;
        this.condition = condition;
    }

    public DefineDirective withCondition(BooleanExpression condition) {
        // TODO: This results in a new, duplicated DeclarationBlock
        return new DefineDirective(getDeclarationBlock().getDeclarations(), global, condition);
    }

    @Override
    public String getName() {
        return "@define" + (global ? " global" : "");
    }

    public boolean isGlobal() {
        return global;
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) throws IOException {
        Boolean cond = condition.evaluate(state);
        if (cond == null || !cond) {
            return null;
        }

        Scope<Expression> scope = state.getVariables();
        if (isGlobal()) {
            while (scope.parent() != null) {
                scope = scope.parent();
            }
        }

        for (Declaration declaration : getDeclarationBlock().getDeclarations()) {
            scope.declare(declaration.getName(), declaration.getExpression());
        }

        return null;
    }
}
