package com.silentmatt.dss.directive;

import com.google.common.collect.ImmutableList;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.css.CssGenericDirective;
import com.silentmatt.dss.css.CssMedium;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.declaration.Declaration;
import com.silentmatt.dss.declaration.DeclarationList;
import com.silentmatt.dss.declaration.Expression;
import com.silentmatt.dss.evaluator.EvaluationState;
import com.silentmatt.dss.media.Medium;
import com.silentmatt.dss.rule.DeclarationBlock;
import com.silentmatt.dss.rule.Rule;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public final class GenericDirective extends Rule {
    public static class Builder {
        private final DeclarationBlock.Builder declarations = new DeclarationBlock.Builder();
        private final ImmutableList.Builder<Rule> rules = ImmutableList.builder();
        private final ImmutableList.Builder<Medium> mediums = ImmutableList.builder();
        private String name;
        private Expression expression;
        
        public void setName(String name) {
            this.name = name;
        }
        
        public void addMedium(Medium medium) {
            this.mediums.add(medium);
        }

        public void setExpression(Expression expression) {
            this.expression = expression;
        }

        public void addDeclaration(Declaration declaration) {
            this.declarations.addDeclaration(declaration);
        }

        public void addRule(Rule rule) {
            this.rules.add(rule);
        }
        
        public GenericDirective build() {
            return new GenericDirective(declarations.build(), rules.build(), mediums.build(), name, expression);
        }
    }

    private final DeclarationBlock declarations;
    private final ImmutableList<Rule> rules;
    private final ImmutableList<Medium> mediums;
    private final String name;
    private final Expression expression;

    public GenericDirective(DeclarationBlock declarations, ImmutableList<Rule> rules, ImmutableList<Medium> mediums, String name, Expression expression) {
        this.declarations = declarations;
        this.rules = rules;
        this.mediums = mediums;
        this.name = name;
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    public List<Medium> getMediums() {
        return mediums;
    }

    public Expression getExpression() {
        return expression;
    }

    public DeclarationList getDeclarations() {
        return declarations.getDeclarations();
    }

    @Override
    public String toString() {
        return toString(0);
    }

    @Override
    public String toString(int nesting) {
        String start = Rule.getIndent(nesting);

        StringBuilder txt = new StringBuilder();

        txt.append(name);

        if (getExpression() != null) {
            txt.append(" ");
            txt.append(getExpression().toString());
        }

        boolean first = true;
        for (Medium m : mediums) {
            if (first) {
                first = false;
                txt.append(" ");
            } else {
                txt.append(", ");
            }
            txt.append(m.toString());
        }

        boolean hasBlock = (this.declarations.getDeclarations().size() > 0 || this.rules.size() > 0);

        if (!hasBlock) {
            txt.append(";");
            return txt.toString();
        }

        txt.append(" {\n").append(start);

        for (Rule dir : rules) {
            txt.append(dir.toString(nesting + 1));
            txt.append("\n");
        }

        first = true;
        for (Declaration dec : declarations.getDeclarations()) {
            if (first) { first = false; } else { txt.append(";"); }
            txt.append("\n\t").append(start);
            txt.append(dec.toString());
        }

        txt.append("\n").append(start).append("}");

        return txt.toString();
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) throws IOException {
        CssGenericDirective result = new CssGenericDirective();

        result.getDeclarations().addAll(declarations.evaluateStyle(state, true).getCssDeclarations(state));
        for (Rule r : rules) {
            result.addRule(r.evaluate(state, container));
        }
        for (Medium m : mediums) {
            result.addMedium(CssMedium.valueOf(m.toString()));
        }
        result.setName(name);
        result.setExpression(expression.evaluate(state, declarations.getDeclarations()));

        return result;
    }

}
