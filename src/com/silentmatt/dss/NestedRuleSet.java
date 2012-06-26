package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 * A RuleSet that is nested inside another RuleSet or ClassDirective.
 *
 * @author Matthew Crumley
 */
@Immutable
public final class NestedRuleSet extends RuleSet {
    private final Combinator combinator;

    /**
     * Constructs a NestedRuleSet from a RuleSet and a Combinator.
     *
     * @param combinator The {@link Combinator} to apply to the nested rule set's
     *                   selectors.
     * @param rs The {@link RuleSet} that is being nested inside another one.
     */
    public NestedRuleSet(Combinator combinator, RuleSet rs) {
        super(rs.getSelectors(), rs.getDeclarationBlock(), rs.getRules());
        this.combinator = combinator;
    }

    /**
     * Gets the combinator that will join the parent rule set with the nested rule set.
     *
     * @return The {@link Combinator}.
     */
    public Combinator getCombinator() {
        return combinator;
    }

    public NestedRuleSet substituteValues(EvaluationState state) {
        List<Declaration> properties = new ArrayList<Declaration>(getDeclarations().toList());
        DeclarationBlock.Builder result = new DeclarationBlock.Builder();
        List<Declaration> list = result.getDeclarations();
        for (int i = 0; i < properties.size(); i++) {
            Declaration dec = properties.get(i);
            properties.set(i, dec.substituteValues(state, new DeclarationList(list), true, true));
        }

        for (int i = 0; i < properties.size(); i++) {
            Declaration declaration = properties.get(i);
            list.add(declaration);
        }

        for (NestedRuleSet rs : getNestedRuleSets()) {
            result.addNestedRuleSet(rs.substituteValues(state));
        }

        DeclarationBlock db = result.build();
        return new NestedRuleSet(getCombinator(), new RuleSet(getSelectors(), db, getRules()));
    }
}
