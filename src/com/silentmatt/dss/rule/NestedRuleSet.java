package com.silentmatt.dss.rule;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.bool.BooleanExpression;
import com.silentmatt.dss.declaration.Declaration;
import com.silentmatt.dss.declaration.DeclarationList;
import com.silentmatt.dss.evaluator.EvaluationState;
import com.silentmatt.dss.selector.Combinator;
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
        List<Declaration> properties = new ArrayList<>(getDeclarations().toList());
        DeclarationBlock.Builder result = new DeclarationBlock.Builder();

        for (Declaration dec : properties) {
            result.addDeclaration(dec.substituteValues(state, DeclarationList.EMPTY, true, true)); // new DeclarationList(ImmutableList.copyOf(result.getDeclarations()))
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
