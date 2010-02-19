package com.silentmatt.dss;

import com.silentmatt.dss.css.CssRule;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public abstract class Rule {
    @Override
    public abstract String toString();
    public abstract String toString(int nesting);

    protected static String getIndent(int nesting) {
        char[] chars = new char[nesting];
        Arrays.fill(chars, '\t');
        return new String(chars);
    }

    public abstract CssRule evaluate(EvaluationState state, List<Rule> container) throws MalformedURLException, IOException;

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
}
