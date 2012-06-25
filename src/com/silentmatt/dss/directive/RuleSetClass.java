package com.silentmatt.dss.directive;

import com.silentmatt.dss.Declaration;
import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.NestedRuleSet;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.RuleSet;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.util.JoinedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public final class RuleSetClass extends ClassDirective {
    private final List<RuleSet> rulesets;

    public RuleSetClass(List<RuleSet> rs) {
        super("<anonymous class>", DeclarationList.EMPTY, true, new DeclarationList(getDeclarations(rs)), getNestedRuleSets(rs));
        this.rulesets = Collections.unmodifiableList(rs);
    }

    private static List<Declaration> getDeclarations(List<RuleSet> rulesets) {
        if (rulesets.isEmpty()) {
            return DeclarationList.EMPTY.toList();
        }

        List<Declaration> result = rulesets.get(0).getDeclarations().toList();
        boolean first = true;
        for (RuleSet rs : rulesets) {
            if (first) {
                first = false;
            }
            else {
                result = new JoinedList<Declaration>(result, rs.getDeclarations().toList());
            }
        }
        return result;
    }

    private static List<NestedRuleSet> getNestedRuleSets(List<RuleSet> rulesets) {
        if (rulesets.isEmpty()) {
            return new ArrayList<NestedRuleSet>(0);
        }

        List<NestedRuleSet> result = rulesets.get(0).getNestedRuleSets();
        boolean first = true;
        for (RuleSet rs : rulesets) {
            if (first) {
                first = false;
            }
            else {
                result = new JoinedList<NestedRuleSet>(result, rs.getNestedRuleSets());
            }
        }
        return result;
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
