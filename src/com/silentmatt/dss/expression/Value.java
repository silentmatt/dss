package com.silentmatt.dss.expression;

import com.silentmatt.dss.Term;
import com.silentmatt.dss.TermType;

/**
 * Represents a dimensioned value.
 *
 * @author Matthew Crumley
 */
public class Value {
    private double scalar;
    private CalculationUnit unit;

    /**
     * Constructs a Value from a number and a unit.
     * 
     * @param scalar The scalar (numeric) part of the value.
     * @param unit The associated {@link CalculationUnit}.
     */
    public Value(double scalar, CalculationUnit unit) {
        this.scalar = scalar * unit.getScale();
        this.unit = CalculationUnit.getCanonicalUnit(unit);
    }

    /**
     * Constructs a Value from a CSS {@link Term}.
     * The Term's {@link TermTypemust} be {@link TermType#Number}.
     *
     * @param term The CSS Term to convert.
     * @throws IllegalArgumentException <code>term</term> is not a number.
     */
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

    /**
     * Constructs a Value from a number and a CSS {@link Unit}.
     *
     * @param scalar The scalar (numeric) part of the value.
     * @param unit The associated CSS Unit.
     */
    public Value(double scalar, com.silentmatt.dss.Unit unit) {
        this.unit = CalculationUnit.fromCssUnit(unit);
        this.scalar = scalar * this.unit.getScale();
        this.unit = CalculationUnit.getCanonicalUnit(this.unit);
    }

    /**
     * Adds two Values.
     * The Values must have compatible units.
     *
     * @param other The Value to add to <code>this</code>
     * @return The sum, <code>this</code> + <code>other</code>.
     * @throws IllegalArgumentException CalculationUnits are not compatible.
     *
     * @see CalculationUnit#isAddCompatible(com.silentmatt.dss.expression.CalculationUnit)
     */
    public Value add(Value other) {
        if (unit.isAddCompatible(other.unit)) {
            return new Value(scalar + other.scalar, unit);
        }
        else {
            throw new IllegalArgumentException("other");
        }
    }

    /**
     * Subtracts two Values.
     * The Values must have compatible units.
     *
     * @param other The Value to subtract from <code>this</code>
     * @return The difference, <code>this</code> - <code>other</code>.
     * @throws IllegalArgumentException CalculationUnits are not compatible.
     *
     * @see CalculationUnit#isAddCompatible(com.silentmatt.dss.expression.CalculationUnit)
     */
    public Value subtract(Value other) {
        if (unit.isAddCompatible(other.unit)) {
            return new Value(scalar - other.scalar, unit);
        }
        else {
            throw new IllegalArgumentException("other");
        }
    }

    /**
     * Multiplies two Values.
     *
     * Unlike add and subtract, the units do not have to be compatible.
     *
     * @param other The Value to multiply <code>this</code> by.
     * @return The product, <code>this</code> * <code>other</code>.
     */
    public Value multiply(Value other) {
        return new Value(scalar * other.scalar, unit.multiply(other.unit));
    }

    /**
     * Divides two Values.
     *
     * Unlike add and subtract, the units do not have to be compatible.
     *
     * @param other The Value to divide <code>this</code> by.
     * @return The quotient, <code>this</code> / <code>other</code>.
     */
    public Value divide(Value other) {
        return new Value(scalar / other.scalar, unit.divide(other.unit));
    }

    /**
     * Negates a Value.
     *
     * @return -<code>this</code>
     */
    public Value negate() {
        return new Value(-scalar, unit);
    }

    /**
     * Converts a Value into a CSS {@link Term}.
     *
     * The Value's unit must be compatible with a valid CSS <code>Unit</code>
     * @return A CSS Term that represents this Value.
     * @throws CalculationException
     * <code>this</code> cannot be represented by a valid CSS unit.
     */
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

    /**
     * Gets a String representation of this Value.
     *
     * @return This Value as a String.
     */
    @Override
    public String toString() {
        return scalar + unit.toString();
    }
}
