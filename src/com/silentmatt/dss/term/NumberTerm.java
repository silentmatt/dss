package com.silentmatt.dss.term;

import com.silentmatt.dss.Unit;

/**
 *
 * @author matt
 */
public class NumberTerm extends Term {
    private double value;
    private Unit unit;

    public NumberTerm(double value) {
        super();
        this.value = value;
        this.unit = Unit.None;
    }

    public char getSign() {
        return value < 0 ? '-' : '+';
    }

    private static final Character PLUS = Character.valueOf('+');
    private static final Character MINUS = Character.valueOf('-');

    public void setSign(Character Sign) {
        if (Sign == null || Sign.equals(PLUS)) {
            value = Math.abs(value);
        }
        else if (Sign.equals(MINUS)) {
            value = -Math.abs(value);
        }
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
