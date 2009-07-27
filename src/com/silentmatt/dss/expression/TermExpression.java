package com.silentmatt.dss.expression;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Function;
import com.silentmatt.dss.Scope;
import com.silentmatt.dss.term.CalculationTerm;
import com.silentmatt.dss.term.FunctionTerm;
import com.silentmatt.dss.term.NumberTerm;
import com.silentmatt.dss.term.Term;

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
        if (value instanceof NumberTerm) {
            return new Value((NumberTerm) value);
        }
        else if (value instanceof CalculationTerm) {
            return ((CalculationTerm) value).getCalculation().calculateValue(variables, parameters);
        }
        else {
            throw new CalculationException("Invalid term in calculation: '" + value + "'");
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
        if (value instanceof FunctionTerm) {
            Function fn = ((FunctionTerm) value).getFunction();
            Expression variable;
            if (fn.getName().equals("const")) {
                variable = variables.get(fn.getExpression().toString());
            }
            else if (fn.getName().equals("param")) {
                if (parameters == null) {
                    return;
                }
                variable = parameters.get(fn.getExpression().toString());
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
