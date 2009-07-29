package com.silentmatt.dss.term;

import com.silentmatt.dss.Color;

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

    public boolean isColor() {
        return false;
    }

    // TODO: Test this
    public Color toColor() {
        return null;
    }
}
