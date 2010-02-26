package com.silentmatt.dss.term;

import com.silentmatt.dss.Unit;

/**
 * A numeric term, with its associated unit.
 *
 * @author matt
 */
public class NumberTerm extends Term {
    /**
     * The numeric, scalar part of the term.
     */
    private double value;

    /**
     * The associtated CSS unit.
     */
    private Unit unit;

    /**
     * Constructs a unitless NumberTerm from a double value.
     * The unit will be {@link Unit.None}.
     *
     * @param value The scalar value of the NumberTerm
     */
    public NumberTerm(double value) {
        super();
        this.value = value;
        this.unit = Unit.None;
    }

    public NumberTerm clone() {
        NumberTerm result = new NumberTerm(value);
        result.setSeperator(getSeperator());
        result.setUnit(unit);
        return result;
    }
    /**
     * Gets the sign of the number.
     *
     * @return '-' if this is less than zero, '+' otherwise.
     */
    public char getSign() {
        return value < 0 ? '-' : '+';
    }

    private static final Character PLUS = Character.valueOf('+');
    private static final Character MINUS = Character.valueOf('-');

    /**
     * Sets the sign of the number.
     * Setting the sign to '+' or null forces the value to be positive. Setting
     * the sign to '-' forces the value to be negative. Any other value will be
     * ignored.
     *
     * @param sign The desired sign of the number, defaulting to '+'
     */
    public void setSign(Character sign) {
        if (sign == null || sign.equals(PLUS)) {
            value = Math.abs(value);
        }
        else if (sign.equals(MINUS)) {
            value = -Math.abs(value);
        }
    }

    /**
     * Gets the scalar value as a floating point value.
     *
     * @return The numeric value of this term.
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the scalar value.
     *
     * @param value The numeric value of this term.
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Gets the CSS unit associated with this term.
     *
     * @return The CSS unit.
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Sets the CSS unit to associtate with this term.
     *
     * @param Unit The CSS unit.
     */
    public void setUnit(Unit Unit) {
        this.unit = Unit;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();

        String valueString = Double.toString(value);
        if (valueString.endsWith(".0")) {
            valueString = valueString.substring(0, valueString.length() - 2);
        }
        txt.append(valueString);
        if (unit != null) {
            txt.append(unit.toString());
        }

        return txt.toString();
    }
}
