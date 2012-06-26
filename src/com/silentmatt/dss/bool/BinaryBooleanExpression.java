package com.silentmatt.dss.bool;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Immutable;

/**
 * Generic BooleanExpression node for binary operators.
 * 
 * @author Matthew Crumley
 */
@Immutable
public final class BinaryBooleanExpression implements BooleanExpression {
    private final BooleanOperation operation;
    private final BooleanExpression left;
    private final BooleanExpression right;

    /**
     * Constructs a BinaryBooleanExpression with an operator and the two operands.
     *
     * @param operation The operation to perform.
     * @param left The left operand.
     * @param right The right operand.
     */
    public BinaryBooleanExpression(BooleanOperation operation, BooleanExpression left, BooleanExpression right) {
        this.operation = operation;
        this.left = left;
        this.right = right;
    }

    @Override
    public Boolean evaluate(EvaluationState state) {
        Boolean leftValue = left.evaluate(state);
        if (leftValue == null) {
            return null;
        }
        Boolean rightValue = right.evaluate(state);
        if (rightValue == null) {
            return null;
        }
        switch (operation) {
        case AND:
            return leftValue && rightValue;
        case OR:
            return leftValue || rightValue;
        case XOR:
            return leftValue ^ rightValue;
        default:
            state.getErrors().SemErr("Unrecognized operation");
            break;
        }
        return null;
    }

    /**
     * Gets the expression as a string.
     *
     * The resulting String will be parsable to an identical BinaryBooleanExpression.
     *
     * @return The String representation of this expression.
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
            case AND:
                sb.append(" && ");
                break;
            case OR:
                sb.append(" || ");
                break;
            case XOR:
                sb.append(" ^ ");
                break;
            default:
                throw new IllegalStateException("Unknown operator: " + operation);
        }

        if (rightPrecidence) { sb.append('('); }
        sb.append(right);
        if (rightPrecidence) { sb.append(')'); }

        return sb.toString();
    }

    @Override
    public int getPrecidence() {
        int precidence;
        switch (operation) {
        case AND: precidence =  2; break;
        case OR:  precidence =  1; break;
        case XOR: precidence =  1; break;
        default:  precidence = -1; break;
        }
        return precidence;
    }
}
