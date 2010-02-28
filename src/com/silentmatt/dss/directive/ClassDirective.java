package com.silentmatt.dss.directive;

import com.silentmatt.dss.Declaration;
import com.silentmatt.dss.DeclarationBlock;
import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.NestedRuleSet;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.Scope;
import com.silentmatt.dss.css.CssRule;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class ClassDirective extends Rule {
    private final String className;
    private final DeclarationList parameters;
    private final DeclarationBlock declarationBlock;
    private final boolean global;

    public ClassDirective(String className, List<Declaration> parameters, boolean global, List<Declaration> declarations, List<NestedRuleSet> nestedRuleSets) {
        this.className = className;
        this.parameters = new DeclarationList(parameters);
        this.global = global;
        this.declarationBlock = new DeclarationBlock(declarations, nestedRuleSets);
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

    public void addParameter(Declaration param) {
        parameters.add(param);
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

        txt.append(declarationBlock.innerString(nesting + 1));

        txt.append("\n" + start + "}");

        return txt.toString();
    }

    public void addDeclartion(Declaration declaration) {
        declarationBlock.addDeclaration(declaration);
    }

    public boolean isGlobal() {
        return global;
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) throws IOException {
        //declarationBlock.getDeclarations().evaluateStyle(state, false);
        DeclarationBlock newBlock = declarationBlock.evaluateStyle(state, false);
        Scope<ClassDirective> scope = state.getClasses();
        if (isGlobal()) {
            while (scope.parent() != null) {
                scope = scope.parent();
            }
        }

        // XXX: Do we really need to create a new instance here?
        scope.declare(className, new ClassDirective(className, parameters, global, newBlock.getDeclarations(), newBlock.getNestedRuleSets()));
        return null;
    }

    public List<NestedRuleSet> getNestedRuleSets() {
        return declarationBlock.getNestedRuleSets();
    }

    @Override
    public String toString() {
        return toString(0);
    }
}
