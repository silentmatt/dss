package com.silentmatt.dss;

/**
 *
 * @author Matthew Crumley
 */
public interface Rule {
    RuleType getRuleType();
    String toString(int nesting);
    String toCssString(int nesting);
}
