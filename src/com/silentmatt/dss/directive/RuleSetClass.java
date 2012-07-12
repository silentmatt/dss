package com.silentmatt.dss.directive;

import com.google.common.collect.ImmutableList;
import com.silentmatt.dss.Declaration;
import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.NestedRuleSet;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.RuleSet;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.util.JoinedList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public final class RuleSetClass extends ClassDirective {
    // FIXME: This is never read
    private final ImmutableList<RuleSet> rulesets;

    public RuleSetClass(ImmutableList<RuleSet> rs) {
        super("<anonymous class>", DeclarationList.EMPTY, true, new DeclarationList(getDeclarations(rs)), getNestedRuleSets(rs), ImmutableList.copyOf(new Rule[0]));
        this.rulesets = rs;
    }

    // FIXME: Making an immutable list is pointless and inefficient here when it's already provably immutable
    private static ImmutableList<Declaration> getDeclarations(List<RuleSet> rulesets) {
        if (rulesets.isEmpty()) {
            return ImmutableList.of();
        }

        List<Declaration> result = rulesets.get(0).getDeclarations().toList();
        boolean first = true;
        for (RuleSet rs : rulesets) {
            if (first) {
                first = false;
            }
            else {
                result = new JoinedList<Declaration>(ImmutableList.copyOf(result), rs.getDeclarations().toList());
            }
        }
        return ImmutableList.copyOf(result);
    }

    // FIXME: Making an immutable list is pointless and inefficient here when it's already provably immutable
    private static ImmutableList<NestedRuleSet> getNestedRuleSets(List<RuleSet> rulesets) {
        if (rulesets.isEmpty()) {
            return ImmutableList.of();
        }

        List<NestedRuleSet> result = rulesets.get(0).getNestedRuleSets();
        boolean first = true;
        for (RuleSet rs : rulesets) {
            if (first) {
                first = false;
            }
            else {
                result = new JoinedList<NestedRuleSet>(ImmutableList.copyOf(result), rs.getNestedRuleSets());
            }
        }
        return ImmutableList.copyOf(result);
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
