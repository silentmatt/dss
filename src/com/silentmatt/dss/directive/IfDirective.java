package com.silentmatt.dss.directive;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.bool.BooleanExpression;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.css.CssRuleList;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class IfDirective extends Rule {
    private final BooleanExpression condition;
    private final List<Rule> ifRules;
    private final List<Rule> elseRules;

    public IfDirective(BooleanExpression condition, List<Rule> ifRules, List<Rule> elseRules) {
        super();
        this.condition = condition;
        this.ifRules = ifRules;
        this.elseRules = elseRules;
    }

    public BooleanExpression getCondition() {
        return condition;
    }

    public List<Rule> getIfRules() {
        return ifRules;
    }

    public List<Rule> getElseRules() {
        return elseRules;
    }

    @Override
    public String toString() {
        return toString(0);
    }

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
        Boolean result = condition.evaluate(state, null);
        if (result == null) {
            state.getErrors().SemErr("Invalid condition: " + condition);
        }
        List<Rule> rules = result ? ifRules : elseRules;

        if (rules != null) {
            CssRuleList crl = new CssRuleList();
            List<CssRule> ruleList = Rule.evaluateRules(state, rules);
            for (CssRule r : ruleList) {
                crl.addRule(r);
            }
            return crl;
        }
        return null;
    }
}
