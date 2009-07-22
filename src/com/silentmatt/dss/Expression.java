package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class Expression {
    private List<Term> terms = new ArrayList<Term>();

    public List<Term> getTerms() {
        return terms;
    }

    public void setTerms(List<Term> Terms) {
        this.terms = Terms;
    }

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
