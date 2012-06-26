package com.silentmatt.dss;

/**
 * A RuleSet that is nested inside another RuleSet or ClassDirective.
 *
 * @author Matthew Crumley
 */
@Immutable
public final class NestedRuleSet extends RuleSet {
    private final Combinator combinator;

    /**
     * Constructs a NestedRuleSet from a RuleSet and a Combinator.
     *
     * @param combinator The {@link Combinator} to apply to the nested rule set's
     *                   selectors.
     * @param rs The {@link RuleSet} that is being nested inside another one.
     */
    public NestedRuleSet(Combinator combinator, RuleSet rs) {
        super(rs.getSelectors(), rs.getDeclarationBlock(), rs.getRules());
        this.combinator = combinator;
    }

    /**
     * Gets the combinator that will join the parent rule set with the nested rule set.
     *
     * @return The {@link Combinator}.
     */
    public Combinator getCombinator() {
        return combinator;
    }
}
