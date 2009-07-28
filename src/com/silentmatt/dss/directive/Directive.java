package com.silentmatt.dss.directive;

import com.silentmatt.dss.Rule;
import com.silentmatt.dss.RuleType;

/**
 *
 * @author Matthew Crumley
 */
public abstract class Directive extends Rule {
    public abstract String getName();
    public abstract DirectiveType getType();

    public RuleType getRuleType() {
        return RuleType.Directive;
    }
}
