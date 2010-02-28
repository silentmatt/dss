package com.silentmatt.dss.directive;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Declaration;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.Scope;
import com.silentmatt.dss.css.CssRule;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class DefineDirective extends DeclarationDirective {
    private final boolean global;

    public DefineDirective(List<Declaration> declarations, boolean global) {
        super(declarations);
        this.global = global;
    }

    public String getName() {
        return "@define" + (global ? " global" : "");
    }

    public boolean isGlobal() {
        return global;
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) throws IOException {
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
