package com.silentmatt.dss.term;

import com.silentmatt.dss.Unit;

/**
 *
 * @author matt
 */
public class NumberTerm extends Term {
    private Character sign;
    private double value;
    private Unit unit;

    public NumberTerm(double value) {
        super();
        this.value = value;
    }

    public Character getSign() {
        return sign;
    }

    public void setSign(Character Sign) {
        this.sign = Sign;
    }

    public double getDoubleValue() {
        return value;
    }

    public String getValue() {
        return Double.toString(value);
    }

    public void setValue(double value) {
        this.value = value;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit Unit) {
        this.unit = Unit;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();

        if (sign != null) { txt.append(sign); }
        txt.append(value);
        if (unit != null) {
            txt.append(unit.toString());
        }

        return txt.toString();
    }
}
