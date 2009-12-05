package com.silentmatt.dss;

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
public class Expression {
    private final List<Term> terms = new ArrayList<Term>();

    public Expression() {
    }

    public Expression(Term term) {
        terms.add(term);
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
                    if (!t.getSeperator().equals(' ')) {
                        txt.append(" ");
                    }
                }
            }
            txt.append(t.toString());
        }
        return txt.toString();
    }

    public Expression substituteValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        Expression newValue = new Expression();

        for (Term primitiveValue : getTerms()) {
            Expression sub = primitiveValue.substituteValues(state, container, withParams, doCalculations);
            if (sub != null) {
                for (Term t : sub.getTerms()) {
                    newValue.getTerms().add(t);
                }
            }
        }

        return newValue;
    }
}
