package com.silentmatt.dss.css;

import java.util.Arrays;

/**
 *
 * @author Matthew Crumley
 */
public abstract class CssRule {
    @Override
    public abstract String toString();
    public abstract String toString(int nesting);
    public abstract String toString(boolean compact, int nesting);

    protected static String getIndent(int nesting) {
        char[] chars = new char[nesting];
        Arrays.fill(chars, '\t');
        return new String(chars);
    }
}
