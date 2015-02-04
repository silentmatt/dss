package com.silentmatt.dss.directive;

import com.google.common.collect.ImmutableList;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.bool.BooleanExpression;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.css.CssRuleList;
import com.silentmatt.dss.evaluator.EvaluationState;
import com.silentmatt.dss.rule.Rule;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public final class IfDirective extends Rule {
    private final BooleanExpression condition;
    private final ImmutableList<Rule> ifRules;
    private final ImmutableList<Rule> elseRules;

    public IfDirective(BooleanExpression condition, ImmutableList<Rule> ifRules, ImmutableList<Rule> elseRules) {
        super();
        this.condition = condition;
        this.ifRules = ifRules;
        this.elseRules = elseRules != null ? elseRules : null;
    }

    public BooleanExpression getCondition() {
        return condition;
    }

    public ImmutableList<Rule> getIfRules() {
        return ifRules;
    }

    public ImmutableList<Rule> getElseRules() {
        return elseRules;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    @Override
    public String toString(int nesting) {
        String start = Rule.getIndent(nesting);

        StringBuilder txt = new StringBuilder(start);
        txt.append("@if ").append(condition).append(" {\n");

        for (Rule rule : ifRules) {
            txt.append(rule.toString(nesting + 1));
            txt.append("\n");
        }

        txt.append(start).append("}");

        if (elseRules != null) {
            txt.append("\n").append(start).append("@else {\n");

            for (Rule rule : elseRules) {
                txt.append(rule.toString(nesting + 1));
                txt.append("\n");
            }

            txt.append(start).append("}");
        }
        return txt.toString();
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) throws IOException {
        Boolean result = condition.evaluate(state);
        if (result != null) {
            List<Rule> rules = result ? ifRules : elseRules;

            if (rules != null) {
                CssRuleList crl = new CssRuleList();
                List<CssRule> ruleList = Rule.evaluateRules(state, rules);
                for (CssRule r : ruleList) {
                    crl.addRule(r);
                }
                return crl;
            }
        }
        else {
            state.getErrors().semanticError("Invalid condition: " + condition);
        }
        return null;
    }
}
