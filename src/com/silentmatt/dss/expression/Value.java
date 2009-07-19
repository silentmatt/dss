package com.silentmatt.dss.expression;

import com.silentmatt.dss.Term;
import com.silentmatt.dss.TermType;

/**
 *
 * @author matt
 */
public class Value {
    private double scalar;
    private CalculationUnit unit;

    public Value(double scalar, CalculationUnit unit) {
        this.scalar = scalar * unit.getScale();
        this.unit = CalculationUnit.getCanonicalUnit(unit);
    }

    public Value(Term term) {
        if (term.getType() != TermType.Number) {
            throw new IllegalArgumentException("term");
        }
        this.unit = CalculationUnit.fromCssUnit(term.getUnit());
        if (this.unit == null) {
            throw new IllegalArgumentException("term");
        }
        char sign = term.getSign() == null ? '+' : term.getSign().charValue();
        this.scalar = Double.parseDouble(sign + term.getValue()) * this.unit.getScale();
        this.unit = CalculationUnit.getCanonicalUnit(this.unit);
    }

    public Value(double scalar, com.silentmatt.dss.Unit unit) {
        this.unit = CalculationUnit.fromCssUnit(unit);
        this.scalar = scalar * this.unit.getScale();
        this.unit = CalculationUnit.getCanonicalUnit(this.unit);
    }

    public Value add(Value other) {
        if (unit.isAddCompatible(other.unit)) {
            return new Value(scalar + other.scalar, unit);
        }
        else {
            throw new IllegalArgumentException("other");
        }
    }

    public Value subtract(Value other) {
        if (unit.isAddCompatible(other.unit)) {
            return new Value(scalar - other.scalar, unit);
        }
        else {
            throw new IllegalArgumentException("other");
        }
    }

    public Value multiply(Value other) {
        return new Value(scalar * other.scalar, unit.multiply(other.unit));
    }

    public Value multiply(double other) {
        return new Value(scalar * other, unit);
    }

    public Value divide(Value other) {
        return new Value(scalar / other.scalar, unit.divide(other.unit));
    }

    public Value divide(double other) {
        return new Value(scalar / other, unit);
    }

    public Value negate() {
        return new Value(-scalar, unit);
    }

    public Term toTerm() throws CalculationException {
        Term t = new Term();
        t.setType(TermType.Number);
        t.setValue(String.valueOf(scalar));
        com.silentmatt.dss.Unit cssUnit = CalculationUnit.toCssUnit(unit);
        if (cssUnit == null) {
            throw new CalculationException("not a valid CSS unit: " + toString());
        }
        t.setUnit(cssUnit);
        return t;
    }

    @Override
    public String toString() {
        return scalar + unit.toString();
    }
}
