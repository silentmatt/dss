package com.silentmatt.dss.term;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.Unit;
import java.text.DecimalFormat;

/**
 * A numeric term, with its associated unit.
 *
 * @author Matthew Crumley
 */
@Immutable
public final class NumberTerm extends Term {
    /**
     * The numeric, scalar part of the term.
     */
    private final double value;

    /**
     * The associated CSS unit.
     */
    private final Unit unit;

    /**
     * Constructs a unitless NumberTerm from a double value.
     * The unit will be {@link Unit.None}.
     *
     * @param value The scalar value of the NumberTerm
     */
    public NumberTerm(double value) {
        super(null);
        this.value = value;
        this.unit = Unit.None;
    }

    /**
     * Constructs a unitless NumberTerm from a double value.
     * The unit will be {@link Unit.None}.
     *
     * @param value The scalar value of the NumberTerm
     * @param sep The separator between the previous term and this one
     */
    public NumberTerm(Character sep, double value) {
        super(sep);
        this.value = value;
        this.unit = Unit.None;
    }

    /**
     * Constructs a NumberTerm with a unit.
     *
     * @param value The scalar value of the NumberTerm
     * @param sep The separator between the previous term and this one
     * @param unit The unit
     */
    public NumberTerm(Character sep, double value, Unit unit) {
        super(sep);
        this.value = value;
        this.unit = unit;
    }

    /**
     * Gets the sign of the number.
     *
     * @return '-' if this is less than zero, '+' otherwise.
     */
    public char getSign() {
        return value < 0 ? '-' : '+';
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
     * Gets the CSS unit associated with this term.
     *
     * @return The CSS unit.
     */
    public Unit getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();

        DecimalFormat format = new DecimalFormat("#.######");
        String valueString = format.format(value);
        txt.append(valueString);
        if (unit != null) {
            txt.append(unit.toString());
        }

        return txt.toString();
    }

    @Override
    public NumberTerm withSeparator(Character separator) {
        return new NumberTerm(separator, getValue(), getUnit());
    }

    public NumberTerm withUnit(Unit unit) {
        return new NumberTerm(getSeperator(), getValue(), unit);
    }

    public NumberTerm withValue(double value) {
        return new NumberTerm(getSeperator(), value, getUnit());
    }
}
