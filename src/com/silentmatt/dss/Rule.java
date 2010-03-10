package com.silentmatt.dss;

import com.silentmatt.dss.css.CssRule;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A DSS Rule.
 * 
 * @author Matthew Crumley
 */
public abstract class Rule {
    /**
     * Gets the string representation of the Rule.
     *
     * @return The serialized form of the DSS Rule. If parsed, it should result
     * in an equivalent Rule.
     *
     * @see #toString(int)
     *
     * @todo Shouldn't this be implemented here, since it should always be "return toString(0);"?
     */
    @Override
    public abstract String toString();

    /**
     * Gets the string representation of the Rule, indented by the specified level.
     * 
     * @param nesting The desired nesting level.
     *
     * @return The serialized form of the Rule.
     *
     * @see #toString()
     */
    public abstract String toString(int nesting);

    /**
     * Gets the leading indentation for the specified level.
     *
     * @param nesting The nesting level.
     *
     * @return The String that should be appended to each line.
     */
    protected static String getIndent(int nesting) {
        char[] chars = new char[nesting];
        Arrays.fill(chars, '\t');
        return new String(chars);
    }

    /**
     * Evaluates the Rule.
     *
     * @param state The current {@link EvaluationState}.
     * @param container The {@link List} of Rules that contains this one.
     *
     * @return The resulting {@link CssRule}. If the rule does not have any CSS
     * output (e.g. @define rules), evalute should return null.
     *
     * @throws MalformedURLException
     * @throws IOException
     */
    public abstract CssRule evaluate(EvaluationState state, List<Rule> container) throws MalformedURLException, IOException;

    /**
     * Evaluates each of the rules in a list.
     * 
     * @param state The current {@link EvaluationState}.
     * @param rules The {@link List} of Rules to evaluate.
     *
     * @return The resulting {@link List} of {@link CssRule}s.
     *
     * @throws MalformedURLException
     * @throws IOException
     */
    public static List<CssRule> evaluateRules(EvaluationState state, List<Rule> rules) throws MalformedURLException, IOException {
        List<CssRule> result = new ArrayList<CssRule>();

        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);
            CssRule r = rule.evaluate(state, rules);
            if (r != null) {
                result.add(r);
            }
        }

        return result;
    }

    /**
     * Filters a list of Rules to get just the RuleSets.
     *
     * @param rules A {@link List} of Rules.
     *
     * @return A {@link List} of the {@link RuleSet}s from the original list.
     */
    public static List<RuleSet> getRuleSets(List<Rule> rules) {
        List<RuleSet> result = new ArrayList<RuleSet>(rules.size());
        for (Rule r : rules) {
            if (r instanceof RuleSet) {
                result.add((RuleSet) r);
            }
        }
        return result;
    }
}
