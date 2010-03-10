/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
            return toString();
        }

        String name = color.toNameString();
        String numeric = color.toString();
        if (name.length() < numeric.length()) {
            return name;
        }
        else {
            return numeric;
        }
    }
}
