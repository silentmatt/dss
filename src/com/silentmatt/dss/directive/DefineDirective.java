package com.silentmatt.dss.directive;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.DSSEvaluator;
import com.silentmatt.dss.Declaration;
import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.Scope;
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
    public String toCssString(int nesting) {
        return "";
    }

    @Override
    public void evaluate(EvaluationState state, List<Rule> container) {
        DeclarationList properties = getDeclarations();
        DSSEvaluator.evaluateStyle(state, properties, true);

        Scope<Expression> scope = state.getVariables();
        if (isGlobal()) {
            while (scope.parent() != null) {
                scope = scope.parent();
            }
        }
        for (int i = 0; i < properties.size(); i++) {
            Declaration property = properties.get(i);
            scope.declare(property.getName(), property.getExpression());
        }
    }
}
