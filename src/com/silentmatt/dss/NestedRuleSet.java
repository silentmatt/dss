package com.silentmatt.dss;

/**
 *
 * @author matt
 */
public class NestedRuleSet extends RuleSet {
    private final Combinator combinator;

    public NestedRuleSet(Combinator combinator, RuleSet rs) {
        super(rs.getSelectors(), rs.getDeclarationBlock(), rs.getRules());
        this.combinator = combinator;
    }

    public Combinator getCombinator() {
        return combinator;
    }
}
