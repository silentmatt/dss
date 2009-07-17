package com.silentmatt.dss;

/**
 *
 * @author matt
 */
public interface Rule {
    RuleType getRuleType();
    String toString(int nesting);
}
