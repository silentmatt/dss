package com.silentmatt.dss.css;

/**
 *
 * @author Matthew Crumley
 */
public class CssLiteralText extends CssRule {
    private final String text;

    public CssLiteralText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public String toString(int nesting) {
        return CssRule.getIndent(nesting) + text;
    }

    @Override
    public String toString(boolean compact, int nesting) {
        return toString(nesting);
    }
}
