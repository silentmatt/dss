package com.silentmatt.dss;

import com.google.common.collect.ImmutableList;
import com.silentmatt.dss.bool.BooleanExpression;
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
    private final BooleanExpression condition;

    /**
     * Constructs a NestedRuleSet from a RuleSet and a Combinator.
     *
     * @param combinator The {@link Combinator} to apply to the nested rule set's
     *                   selectors.
     * @param rs The {@link RuleSet} that is being nested inside another one.
     */
    public NestedRuleSet(Combinator combinator, RuleSet rs, BooleanExpression condition) {
        super(rs.getSelectors(), rs.getDeclarationBlock());
        this.combinator = combinator;
        this.condition = condition;
    }

    /**
     * Gets the combinator that will join the parent rule set with the nested rule set.
     *
     * @return The {@link Combinator}.
     */
    public Combinator getCombinator() {
        return combinator;
    }

    /**
     * Gets the condition.
     *
     * @return The condition.
     */
    public BooleanExpression getCondition() {
        return condition;
    }
    
    public NestedRuleSet withCondition(BooleanExpression condition) {
        return new NestedRuleSet(getCombinator(), new RuleSet(getSelectors(), getDeclarationBlock()), condition);
    }

    public NestedRuleSet substituteValues(EvaluationState state) {
        List<Declaration> properties = new ArrayList<Declaration>(getDeclarations().toList());
        DeclarationBlock.Builder result = new DeclarationBlock.Builder();
        List<Declaration> list = result.getDeclarations();
        for (int i = 0; i < properties.size(); i++) {
            Declaration dec = properties.get(i);
            // FIXME: list -> result.getDeclarations()?
            properties.set(i, dec.substituteValues(state, new DeclarationList(ImmutableList.copyOf(list)), true, true));
        }

        for (int i = 0; i < properties.size(); i++) {
            Declaration declaration = properties.get(i);
            list.add(declaration); // FIXME: list.addDeclaration
        }

        for (NestedRuleSet rs : getNestedRuleSets()) {
            Boolean cond = rs.getCondition().evaluate(state);
            if (cond != null && cond) {
                result.addNestedRuleSet(rs.substituteValues(state).withCondition(BooleanExpression.TRUE));
            }
        }

        DeclarationBlock db = result.build();
        return new NestedRuleSet(getCombinator(), new RuleSet(getSelectors(), db), getCondition());
    }
}
