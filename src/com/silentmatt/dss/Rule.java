package com.silentmatt.dss;

import java.util.Arrays;

/**
 *
 * @author Matthew Crumley
 */
public abstract class Rule {
    public abstract RuleType getRuleType();
    public abstract String toString(int nesting);
    public abstract String toCssString(int nesting);

    protected static String getIndent(int nesting) {
        char[] chars = new char[nesting];
        Arrays.fill(chars, '\t');
        return new String(chars);
    }
}
