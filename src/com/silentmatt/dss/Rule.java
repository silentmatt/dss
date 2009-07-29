package com.silentmatt.dss;

import java.io.IOException;
import java.net.MalformedURLException;
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
    public abstract String toCssString(int nesting);

    protected static String getIndent(int nesting) {
        char[] chars = new char[nesting];
        Arrays.fill(chars, '\t');
        return new String(chars);
    }

    public abstract void evaluate(DSSEvaluator.EvaluationState state, List<Rule> container) throws MalformedURLException, IOException;

    public static void evaluateRules(DSSEvaluator.EvaluationState state, List<Rule> rules) throws MalformedURLException, IOException {
        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);
            rule.evaluate(state, rules);
        }
    }
}
