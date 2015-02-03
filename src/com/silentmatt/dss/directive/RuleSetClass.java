package com.silentmatt.dss.directive;

import com.google.common.collect.ImmutableList;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.declaration.Declaration;
import com.silentmatt.dss.declaration.DeclarationList;
import com.silentmatt.dss.evaluator.EvaluationState;
import com.silentmatt.dss.rule.NestedRuleSet;
import com.silentmatt.dss.rule.Rule;
import com.silentmatt.dss.rule.RuleSet;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public final class RuleSetClass extends ClassDirective {
    public RuleSetClass(ImmutableList<RuleSet> rs) {
        super("<anonymous class>", DeclarationList.EMPTY, true, new DeclarationList(getDeclarations(rs)), getNestedRuleSets(rs), ImmutableList.copyOf(new Rule[0]));
    }

    private static ImmutableList<Declaration> getDeclarations(List<RuleSet> rulesets) {
        if (rulesets.isEmpty()) {
            return ImmutableList.of();
        }

        ImmutableList.Builder<Declaration> result = ImmutableList.builder();
        for (RuleSet rs : rulesets) {
            result.addAll(rs.getDeclarations().toList());
        }
        return result.build();
    }

    private static ImmutableList<NestedRuleSet> getNestedRuleSets(List<RuleSet> rulesets) {
        if (rulesets.isEmpty()) {
            return ImmutableList.of();
        }

        ImmutableList.Builder<NestedRuleSet> result = ImmutableList.builder();
        for (RuleSet rs : rulesets) {
            result.addAll(rs.getNestedRuleSets());
        }
        return result.build();
    }

    @Override
    public String getName() {
        return "@anonymous-class";
    }

    @Override
    public String toString(int nesting) {
        return "";
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) {
        // This is a pseudo-directive, so it doesn't get evaluated
        throw new UnsupportedOperationException("Cannot evaluate a RuleSetClass");
    }

    @Override
    public String toString() {
        return toString(0);
    }
}
