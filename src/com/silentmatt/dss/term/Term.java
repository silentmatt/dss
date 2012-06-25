package com.silentmatt.dss.term;

import com.silentmatt.dss.Color;
import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Immutable;

/**
 * A DSS term.
 * Terms make up expressions and have an optional separator.
 * For example, in the "font-family" property, font names are separated by commas.
 * The separator is associated with the term to the right.
 *
 * @author Matthew Crumley
 */
@Immutable
public abstract class Term implements Cloneable {
    /**
     * The separator preceeding this term.
     */
    private final Character seperator;
    
    public Term(Character seperator) {
        this.seperator = seperator;
    }

    /**
     * Gets the separator.
     *
     * @return The character separating this term from the previous term, or <code>null</code>.
     */
    public Character getSeperator() {
        return seperator;
    }

    /**
     * Gets a new Term with the specified separator.
     *
     * @param seperator The character separating this term from the previous term, or <code>null</code>.
     */
    public abstract Term withSeparator(Character separator);

    /**
     * Converts this term into an expression.
     *
     * @return A new Expression with <code>this</code> as its single term.
     */
    public Expression toExpression() {
        return new Expression(this);
    }

    /**
     * Gets the result of substituting any DSS terms.
     * The default implementation is to return <code>toExpression()</code>.
     *
     * @param state The current evaluation state
     * @param withParams <code>true</code> if parameters should be evaluated
     * @param doCalculations <code>true</code> if calculations should be evaluated
     * @return The resulting expression
     */
    // XXX: Most Terms are going to return a single Term, but ConstTerm and ParamTerm need a full expression...
    public Expression substituteValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        return toExpression();
    }

    /**
     * Returns true if this term can be treated as a color.
     *
     * The default implementation calls toColor() and tests the result for null.
     * Subclasses do not need to override this method, since the default
     *
     * @return <code>true</code> if this is a color
     *
     * @see #toColor()
     */
    public boolean isColor() {
        return toColor() != null;
    }

    /**
     * Converts this term into a color.
     *
     * The default implementation of <code>isColor</code> calls <code>toColor</code>,
     * so overriding implementations should not call <code>isColor</code> unless
     * they override both methods.
     *
     * @return The color corresponding to this term, or null if it cannot be
     *         converted into a color. The default implementation simply returns
     *         <code>null</code>.
     */
    public Color toColor() {
        return null;
    }

    /**
     * Converts this term  into a CSS-compatible String.
     *
     * @return The String representation of this term.
     */
    @Override
    public abstract String toString();
}
