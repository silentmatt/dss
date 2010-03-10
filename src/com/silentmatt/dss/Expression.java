package com.silentmatt.dss;

import com.silentmatt.dss.css.CssColorTerm;
import com.silentmatt.dss.css.CssExpression;
import com.silentmatt.dss.css.CssTerm;
import com.silentmatt.dss.term.Term;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the value part of a declaration (right-hand side).
 *
 * An expression is a list of {@link Term}s, separated by spaces, commas, or slashes.
 *
 * @author Matthew Crumley
 */
public class Expression implements Cloneable {
    private final List<Term> terms = new ArrayList<Term>();

    /**
     * Constructs an empty Expression.
     */
    public Expression() {
    }

    /**
     * Constructs an Expression containing a single Term.
     *
     * @param term The {@link Term} to create the expression from.
     *
     * @see Term#toExpression()
     */
    public Expression(Term term) {
        terms.add(term);
    }

    /**
     * Creates a deep copy of the Expression.
     *
     * @return A new Expression that is identical to this one.
     */
    @Override
    public Expression clone() {
        Expression result = new Expression();
        for (Term t : terms) {
            result.terms.add(t.clone());
        }
        return result;
    }

    /**
     * Gets the child terms of the expression.
     *
     * @return The Terms contained in the expression
     */
    public List<Term> getTerms() {
        return terms;
    }

    /**
     * Gets the expression as a String.
     *
     * @return A string of the form "term [&lt;separator&gt; term]*".
     */
    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        boolean first = true;
        for (Term t : terms) {
            if (first) {
                first = false;
            } else {
                if (t.getSeperator() == null) {
                    txt.append(" ");
                }
                else {
                    txt.append(t.getSeperator());
                    if (t.getSeperator() == null || t.getSeperator().equals(',')) {
                        txt.append(" ");
                    }
                }
            }
            txt.append(t.toString());
        }
        return txt.toString();
    }

    /**
     * Substitutes the values of DSS-specific terms into the expression.
     *
     * @param state The current {@link EvaluationState}.
     * @param container The {@link DeclarationList} that the expression is contained in.
     * @param withParams true if param terms should be substituted.
     * @param doCalculations true if calc terms should be substituted.
     * @return
     */
    public Expression substituteValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        Expression newValue = new Expression();

        for (Term primitiveValue : getTerms()) {
            Expression sub = primitiveValue.substituteValues(state, container, withParams, doCalculations);
            if (sub != null) {
                for (Term t : sub.getTerms()) {
                    newValue.getTerms().add(t);
                }
            }
            else {
                state.getErrors().SemErr("Error evaluating '" + primitiveValue + "'");
            }
        }

        return newValue;
    }

    /**
     * Evaluate the Expression, converting it to a CssExpression.
     *
     * @param state The current {@link EvaluationState}.
     * @param container The {@link DeclarationList} the Expression is contained in.
     *
     * @return The {@link CssExpression} result of the evaluation.
     */
    public CssExpression evaluate(EvaluationState state, DeclarationList container) {
        // TODO: should doCalculations be true?
        Expression newValue = substituteValues(state, container, state.getParameters() != null, true);
        CssExpression result = new CssExpression();
        for (Term t : newValue.getTerms()) {
            CssTerm cssTerm;
            if (t.isColor()) {
                cssTerm = new CssColorTerm(t.toColor());
            }
            else {
                cssTerm = new CssTerm(t.toString());
            }
            cssTerm.setSeperator(t.getSeperator());
            result.getTerms().add(cssTerm);
        }
        return result;
    }
}
