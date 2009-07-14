package com.silentmatt.dss;

import java.util.List;

/**
 *
 * @author matt
 */
public interface RuleSetContainer {
    List<RuleSet> getRuleSets();
    void setRuleSets(List<RuleSet> ruleSet);
}
