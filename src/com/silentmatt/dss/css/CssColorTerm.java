package com.silentmatt.dss.css;

import com.silentmatt.dss.Color;

/**
 *
 * @author Matthew Crumley
 */
public class CssColorTerm extends CssTerm {
    private final Color color;

    public CssColorTerm(Color c) {
        super(c.toString());
        this.color = c;
    }

    @Override
    public String toString(boolean compact) {
        if (!compact) {
            return color.toRGBColor().toString();
            //return toString();
        }

        String name = color.toRGBColor().toNameString();
        String numeric = color.toRGBColor().toString();
        if (name.length() < numeric.length()) {
            return name;
        }
        else {
            return numeric;
        }
    }
}
