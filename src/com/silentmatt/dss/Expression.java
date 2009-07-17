package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author matt
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
                txt.append(t.getSeperator() != null ? t.getSeperator().toString() : " ");
            }
            txt.append(t.toString());
        }
        return txt.toString();
    }
}
