package com.silentmatt.dss.expression;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Scope;

/**
 *
 * @author Matthew Crumley
 */
public class BinaryExpression implements CalcExpression {
    private Operation op;
    private CalcExpression left;
    private CalcExpression right;

    public BinaryExpression(Operation op, CalcExpression left, CalcExpression right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    public Value calculateValue(Scope<Expression> variables, Scope<Expression> parameters) throws CalculationException {
        try {
            Value l = left.calculateValue(variables, parameters);
            Value r = right.calculateValue(variables, parameters);
            switch (op) {
            case Add:
                return l.add(r);
            case Subtract:
                return l.subtract(r);
            case Multiply:
                return l.multiply(r);
            case Divide:
                return l.divide(r);
            default:
                throw new CalculationException("Unrecognized operation");
            }
        } catch (IllegalArgumentException ex) {
            throw new CalculationException("incompatible units");
        }
    }

    public void substituteValues(Scope<Expression> variables, Scope<Expression> parameters) throws CalculationException {
        left.substituteValues(variables, parameters);
        right.substituteValues(variables, parameters);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean lp = left.getPrecidence() < getPrecidence();
        boolean rp = right.getPrecidence() <= getPrecidence();

        if (lp) { sb.append('('); }
        sb.append(left);
        if (lp) { sb.append(')'); }

        switch (op) {
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
        }

        if (rp) { sb.append('('); }
        sb.append(right);
        if (rp) { sb.append(')'); }

        return sb.toString();
    }

    public int getPrecidence() {
    switch (op) {
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
