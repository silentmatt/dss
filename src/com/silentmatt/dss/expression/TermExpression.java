package com.silentmatt.dss.expression;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Function;
import com.silentmatt.dss.Scope;
import com.silentmatt.dss.Term;
import com.silentmatt.dss.TermType;

/**
 * A CalcExpression that evaluates to a simple CSS {@link Term}.
 *
 * @author Matthew Crumley
 */
public class TermExpression implements CalcExpression {
    private Term value;

    /**
     * Constructs a TermExpression from a CSS Term.
     *
     * @param value The Term this expression will return.
     */
    public TermExpression(Term value) {
        this.value = value;
    }

    public Value calculateValue(Scope<Expression> variables, Scope<Expression> parameters) throws CalculationException {
        substituteValues(variables, parameters);
        if (value.getType() == TermType.Number) {
            return new Value(value);
        }
        else if (value.getType() == TermType.Calculation) {
            return value.getCalculation().calculateValue(variables, parameters);
        }
        else {
            throw new CalculationException("Invalid term type: " + value.getType());
        }
    }

    public int getPrecidence() {
        return 3;
    }

    /**
     * Gets the Term's string representation.
     *
     * @return The Term as a String.
     */
    @Override
    public String toString() {
        return this.value.toString();
    }

    public void substituteValues(Scope<Expression> variables, Scope<Expression> parameters) throws CalculationException {
        if (value.getType() == TermType.Function) {
            Function fn = value.getFunction();
            Expression variable = null;
            if (fn.getName().equals("const")) {
                variable = variables.get(fn.getExpression().toString());
            }
            else if (fn.getName().equals("param")) {
                if (parameters != null) {
                    variable = parameters.get(fn.getExpression().toString());
                }
                else {
                    return;
                }
            }
            else {
                throw new CalculationException("unrecognized function");
            }

            if (variable == null) {
                throw new CalculationException("missing value: " + fn.toString());
            }
            if (variable.getTerms().size() > 1) {
                throw new CalculationException("not a single value: " + fn.toString());
            }

            value = variable.getTerms().get(0);
        }
    }
}
