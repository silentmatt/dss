package com.silentmatt.dss.term;

import com.silentmatt.dss.Color;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;

/**
 * A DSS term.
 * Terms make up expressions and have an optional separator.
 * For example, in the "font-family" property, font names are separated by commas.
 * The separator is associtated with the term to the right.
 *
 * @author Matthew Crumley
 */
public abstract class Term {
    /**
     * The separator preceeding this term.
     */
    private Character seperator;

    /**
     * Gets the separator.
     *
     * @return The character separating this term from the previous term, or <code>null</code>.
     */
    public Character getSeperator() {
        return seperator;
    }

    /**
     * Sets the separator.
     *
     * @param Seperator The separator to preceed this term.
     */
    public void setSeperator(Character seperator) {
        this.seperator = seperator;
    }

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
    public Expression substituteValues(EvaluationState state, boolean withParams, boolean doCalculations) {
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
