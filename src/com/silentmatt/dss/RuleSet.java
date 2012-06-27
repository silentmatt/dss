package com.silentmatt.dss;

import com.silentmatt.dss.bool.BooleanExpression;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.css.CssRuleList;
import com.silentmatt.dss.css.CssRuleSet;
import com.silentmatt.dss.util.JoinedSelectorList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A DSS rule set.
 * 
 * @author Matthew Crumley
 */
@Immutable
public class RuleSet extends Rule {
    public static class Builder {
        private final List<Selector> selectors = new ArrayList<Selector>();
        private DeclarationBlock declarationBlock;

        public Builder setDeclarationBlock(DeclarationBlock declarations) {
            declarationBlock = declarations;
            return this;
        }

        /**
        * Adds a selector to the RuleSet.
        *
        * @param sel The {@link Selector} to add.
        */
        public Builder addSelector(Selector sel) {
            selectors.add(sel);
            return this;
        }

        public RuleSet build() {
            if (declarationBlock == null) {
                declarationBlock = new DeclarationBlock.Builder().build();
            }
            return new RuleSet(selectors, declarationBlock);
        }
    }

    private final List<Selector> selectors;
    private final DeclarationBlock declarationBlock;

    /**
     * Constructs a RuleSet with the specified selector list, declaration block, and nested rules.
     *
     * @param selectors The {@link List} of {@link Selector}s.
     * @param block The {@link DeclarationBlock}.
     * @param rules A {@link List} of nested DSS {@link Rule}s.
     */
    public RuleSet(List<Selector> selectors, DeclarationBlock block) {
        this.declarationBlock = block;
        this.selectors = Collections.unmodifiableList(selectors);
    }

    /**
     * Gets the DeclarationBlock.
     *
     * @return The rule set's {@link DeclarationBlock}.
     */
    public DeclarationBlock getDeclarationBlock() {
        return declarationBlock;
    }

    /**
     * Gets the rule set's selectors.
     *
     * @return A {@link List} of {@link Selector}s.
     */
    public List<Selector> getSelectors() {
        return selectors;
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
                Boolean cond = rs.getCondition().evaluate(state);
                if (cond != null && cond) {
                    List<Selector> joinedSelectors = new JoinedSelectorList(getSelectors(), rs.getCombinator(), rs.getSelectors());
                    RuleSet finalRuleSet = new RuleSet(joinedSelectors, rs.getDeclarationBlock());
                    //result.addRule(new NestedRuleSet(getSelectors(), rs.getCombinator(), rs).evaluate(state, null));
                    result.addRule(finalRuleSet.evaluate(state, container));
                }
            }
        }
        finally {
            state.popScope();
        }

        return result.getRules().size() == 1 ? result.getRules().get(0) : result;
    }

    /**
     * Gets a Declaration by name.
     * @param name The property name to get.
     * @return A {@link Declaration} with the specified name, or null if none exists.
     */
    public Declaration getDeclaration(String name) {
        return declarationBlock.getDeclaration(name);
    }

    /**
     * Gets the list of Declarations from the RuleSet's block.
     *
     * @return The RuleSet's DeclarationList.
     */
    public DeclarationList getDeclarations() {
        return declarationBlock.getDeclarations();
    }

    /**
     * Gets the nested RuleSets.
     * 
     * @return A {@link List} of {@link NestedRuleSet}s.
     */
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

    /**
     * Gets a property value by name.
     *
     * @param name The name of the property to get.
     *
     * @return The property's value as an {@link Expression}, or null if it
     * doesn't exist.
     */
    public Expression getValue(String name) {
        return declarationBlock.getValue(name);
    }

    @Override
    public String toString() {
        return toString(0);
    }

    @Override
    public String toString(int nesting) {
        String start = Rule.getIndent(nesting);

        StringBuilder txt = new StringBuilder(start);
        txt.append(Selector.join(getSelectors()));
        txt.append(" {");

        for (Rule dir : getRules()) {
            txt.append("\n\t").append(start);
            txt.append(dir.toString(nesting + 1));
        }

        txt.append(declarationBlock.innerString(nesting));

        txt.append("\n").append(start).append("}");

        return txt.toString();
    }
}
