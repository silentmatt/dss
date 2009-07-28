package com.silentmatt.dss.expression;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Scope;

/**
 * A CalcExpression that represents a binary operation.
 *
 * @author Matthew Crumley
 */
public class BinaryExpression implements CalcExpression {
    private final Operation operation;
    private final CalcExpression left;
    private final CalcExpression right;

    /**
     * Constructs a BinaryExpression with an operator and the two operands.
     *
     * @param operation The oparation to perform.
     * @param left The left operand.
     * @param right The right operand.
     */
    public BinaryExpression(Operation operation, CalcExpression left, CalcExpression right) {
        this.operation = operation;
        this.left = left;
        this.right = right;
    }

    public Value calculateValue(Scope<Expression> variables, Scope<Expression> parameters) throws CalculationException {
        try {
            Value leftValue = left.calculateValue(variables, parameters);
            Value rightValue = right.calculateValue(variables, parameters);
            switch (operation) {
            case Add:
                return leftValue.add(rightValue);
            case Subtract:
                return leftValue.subtract(rightValue);
            case Multiply:
                return leftValue.multiply(rightValue);
            case Divide:
                return leftValue.divide(rightValue);
            default:
                throw new CalculationException("Unrecognized operation");
            }
        } catch (IllegalArgumentException ex) {
            throw new CalculationException("incompatible units", ex);
        }
    }

    public void substituteValues(Scope<Expression> variables, Scope<Expression> parameters) throws CalculationException {
        left.substituteValues(variables, parameters);
        right.substituteValues(variables, parameters);
    }

    /**
     * Gets the expression as a string.
     *
     * The resulting String will be parsable to an identical BinaryExpression.
     *
     * @return The String repressentation of this expression.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean leftPrecidence = left.getPrecidence() < getPrecidence();
        boolean rightPrecidence = right.getPrecidence() <= getPrecidence();

        if (leftPrecidence) { sb.append('('); }
        sb.append(left);
        if (leftPrecidence) { sb.append(')'); }

        switch (operation) {
            case Add:
                sb.append(" + ");
                break;
            case Subtract:
                sb.append(" - ");
                break;
            case Multiply:
                sb.append(" * ");
                break;
            case Divide:
                sb.append(" / ");
                break;
            default:
                throw new IllegalStateException("Unknown operator: " + operation);
        }

        if (rightPrecidence) { sb.append('('); }
        sb.append(right);
        if (rightPrecidence) { sb.append(')'); }

        return sb.toString();
    }

    public int getPrecidence() {
    switch (operation) {
        case Add:
        case Subtract:
            return 1;
        case Multiply:
        case Divide:
            return 2;
        default:
            return -1;
        }
    }
}
