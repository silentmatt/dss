package com.silentmatt.dss;

import java.util.List;

/**
 *
 * @author matt
 */
public interface DirectiveContainer {
    List<Directive> getDirectives();
    void setDirectives(List<Directive> ruleSet);
}
