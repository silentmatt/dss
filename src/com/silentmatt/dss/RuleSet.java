package com.silentmatt.dss;

import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.css.CssRuleList;
import com.silentmatt.dss.css.CssRuleSet;
import com.silentmatt.dss.util.JoinedSelectorList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class RuleSet extends Rule {
    private final List<Selector> selectors;
    private final List<Rule> rules;
    private final DeclarationBlock declarationBlock;

    public RuleSet(List<Selector> selectors, DeclarationBlock block, List<Rule> rules) {
        this.declarationBlock = block;
        this.selectors = selectors;
        this.rules = rules;
    }

    public RuleSet(List<Selector> selectors, DeclarationBlock block) {
        this(selectors, block, new ArrayList<Rule>());
    }

     public RuleSet() {
        this(new ArrayList<Selector>(), new DeclarationBlock(), new ArrayList<Rule>());
    }

    public DeclarationBlock getDeclarationBlock() {
        return declarationBlock;
    }

    public List<Selector> getSelectors() {
        return selectors;
    }

    public void addDeclarations(List<Declaration> declarations) {
        declarationBlock.addDeclarations(declarations);
    }

    public void addNestedRuleSet(Combinator cb, RuleSet nested) {
        declarationBlock.addNestedRuleSet(cb, nested);
    }

    public void addRule(Rule directive) {
        rules.add(directive);
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) throws IOException{
        CssRuleList result = new CssRuleList();
        state.pushScope(declarationBlock.getRuleSetScope());
        try {
            for (Rule dir : this.getRules()) {
                dir.evaluate(state, null);
            }

            CssRuleSet crs = new CssRuleSet();
            for (Selector s : getSelectors()) {
                crs.getSelectors().add(s.evaluate());
            }

            DeclarationBlock resultBlock = declarationBlock.evaluateStyle(state, true);

            // TODO: This is already evaluated, we just need to convert it to CSS...
            crs.addDeclarations(resultBlock.getCssDeclarations(state));

            result.addRule(crs);

            for (NestedRuleSet rs : resultBlock.getNestedRuleSets()) {
                List<Selector> joinedSelectors = new JoinedSelectorList(getSelectors(), rs.getCombinator(), rs.getSelectors());
                RuleSet finalRuleSet = new RuleSet(joinedSelectors, rs.getDeclarationBlock());
                //result.addRule(new NestedRuleSet(getSelectors(), rs.getCombinator(), rs).evaluate(state, null));
                result.addRule(finalRuleSet.evaluate(state, container));
            }
        }
        finally {
            state.popScope();
        }

        return result.getRules().size() == 1 ? result.getRules().get(0) : result;
    }

    public Declaration getDeclaration(String name) {
        return declarationBlock.getDeclaration(name);
    }

    public DeclarationList getDeclarations() {
        return declarationBlock.getDeclarations();
    }

    public List<NestedRuleSet> getNestedRuleSets() {
        return declarationBlock.getNestedRuleSets();
    }

    public List<Rule> getRules() {
        return rules;
    }

    public Expression getValue(String name) {
        return declarationBlock.getValue(name);
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int nesting) {
        String start = Rule.getIndent(nesting);

        StringBuilder txt = new StringBuilder(start);
        txt.append(Selector.join(getSelectors()));
        txt.append(" {");

        for (Rule dir : getRules()) {
            txt.append("\n\t" + start);
            txt.append(dir.toString(nesting + 1));
        }

        txt.append(declarationBlock.innerString(nesting));

        txt.append("\n" + start + "}");

        return txt.toString();
    }
}
