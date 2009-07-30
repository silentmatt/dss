package com.silentmatt.dss.term;

import com.silentmatt.dss.Color;
import com.silentmatt.dss.Expression;

/**
 *
 * @author Matthew Crumley
 */
public abstract class Term {
    private Character seperator;

    public Character getSeperator() {
        return seperator;
    }

    public void setSeperator(Character Seperator) {
        this.seperator = Seperator;
    }

    public Expression toExpression() {
        Expression result = new Expression();
        result.getTerms().add(this);
        return result;
    }

    public boolean isColor() {
        return toColor() != null;
    }

    public Color toColor() {
        return null;
    }
}
