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
    private List<Term> terms = new ArrayList<Term>();

    /**
     * Gets the child terms of the expression.
     *
     * @return The Terms contained in the expression
     */
    public List<Term> getTerms() {
        return terms;
    }

    /**
     * Sets the list of Terms in the expression.
     *
     * @param Terms The List of Terms.
     */
    public void setTerms(List<Term> Terms) {
        this.terms = Terms;
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
}
