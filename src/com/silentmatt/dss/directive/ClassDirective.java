package com.silentmatt.dss.directive;

import com.silentmatt.dss.Declaration;
import com.silentmatt.dss.DeclarationBlock;
import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.NestedRuleSet;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.RuleSet;
import com.silentmatt.dss.Scope;
import com.silentmatt.dss.css.CssRule;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public class ClassDirective extends Rule {
    private final String className;
    private final DeclarationList parameters;
    private final DeclarationBlock declarationBlock;
    private final boolean global;

    public ClassDirective(String className, DeclarationList parameters, boolean global, DeclarationList declarations, List<NestedRuleSet> nestedRuleSets, List<Rule> rules) {
        this.className = className;
        this.parameters = parameters;
        this.global = global;
        this.declarationBlock = new DeclarationBlock(declarations, nestedRuleSets, rules);
    }

    public ClassDirective(String className, DeclarationList parameters, boolean global, DeclarationBlock declarations) {
        this.className = className;
        this.parameters = parameters;
        this.global = global;
        this.declarationBlock = declarations;
    }

    public String getName() {
        return "@class";
    }

    public String getClassName() {
        return className;
    }

    public DeclarationList getParameters(DeclarationList arguments) {
        return parameters;
    }

    public DeclarationList getDeclarations(DeclarationList arguments) {
        return declarationBlock.getDeclarations();
    }

    @Override
    public String toString(int nesting) {
        String start = Rule.getIndent(nesting);

        StringBuilder txt = new StringBuilder(start);
        txt.append("@class ");
        txt.append(getClassName());
        if (!parameters.isEmpty()) {
            txt.append("<");
            boolean first = true;
            for (Declaration param : parameters) {
                if (!first) {
                    txt.append("; ");
                }
                first = false;
                txt.append(param.getExpression() == null ? param.getName() : param.toString());
            }
            txt.append(">");
        }
        if (isGlobal()) {
            txt.append(" global");
        }
        txt.append(" {");

        for (Rule dir : getRules()) {
            txt.append("\n\t").append(start);
            txt.append(dir.toString(nesting + 1));
        }

        txt.append(declarationBlock.innerString(nesting + 1));

        txt.append("\n").append(start).append("}");

        return txt.toString();
    }

    public boolean isGlobal() {
        return global;
    }

    protected List<RuleSet> getRuleSetScope(EvaluationState state) {
        List<RuleSet> result = new ArrayList<RuleSet>(getNestedRuleSets().size());
        for (NestedRuleSet nrs : getNestedRuleSets()) {
            Boolean cond = nrs.getCondition().evaluate(state);
            if (cond != null && cond) {
                result.add(nrs);
            }
        }
        return result;
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) throws IOException {
        DeclarationBlock newBlock = null;
        List<NestedRuleSet> nested = new ArrayList<NestedRuleSet>();

        state.pushScope(getRuleSetScope(state));
        try {
            for (Rule dir : this.getRules()) {
                dir.evaluate(state, null);
            }

            //declarationBlock.getDeclarations().evaluateStyle(state, false);
            newBlock = declarationBlock.evaluateStyle(state, false);
            for (NestedRuleSet rs : newBlock.getNestedRuleSets()) {
                //Boolean cond = rs.getCondition().evaluate(state);
                //if (cond != null && cond) {
                    nested.add(new NestedRuleSet(rs.getCombinator(), new RuleSet(rs.getSelectors(), rs.getDeclarationBlock().evaluateStyle(state, false)), rs.getCondition()));
                //}
            }
        }
        finally {
            state.popScope();
        }

        Scope<ClassDirective> scope = state.getClasses();
        if (isGlobal()) {
            while (scope.parent() != null) {
                scope = scope.parent();
            }
        }

        if (newBlock != null) {
            // XXX: Do we really need to create a new instance here?
            scope.declare(className, new ClassDirective(className, parameters, global, newBlock.getDeclarations(), nested, getRules()));
        }
        else {
            throw new RuntimeException("Error evaluating class " + className);
        }

        return null;
    }

    public List<NestedRuleSet> getNestedRuleSets() {
        return declarationBlock.getNestedRuleSets();
    }

    /**
     * Gets the nested DSS directives.
     *
     * @return A {@link List} of {@link Rule}s.
     */
    public List<Rule> getRules() {
        return declarationBlock.getRules();
    }

    @Override
    public String toString() {
        return toString(0);
    }
}
