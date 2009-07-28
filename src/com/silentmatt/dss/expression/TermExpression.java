package com.silentmatt.dss.expression;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Scope;
import com.silentmatt.dss.parser.ErrorReporter;
import com.silentmatt.dss.term.CalculationTerm;
import com.silentmatt.dss.term.ConstTerm;
import com.silentmatt.dss.term.NumberTerm;
import com.silentmatt.dss.term.ParamTerm;
import com.silentmatt.dss.term.ReferenceTerm;
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

    public Value calculateValue(Scope<Expression> variables, Scope<Expression> parameters, ErrorReporter errors) {
        substituteValues(variables, parameters, errors);
        if (value instanceof NumberTerm) {
            return new Value((NumberTerm) value);
        }
        else if (value instanceof CalculationTerm) {
            return ((CalculationTerm) value).getCalculation().calculateValue(variables, parameters, errors);
        }

        errors.SemErr("Invalid term in calculation: '" + value + "'");
        return null;
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

    public void substituteValues(Scope<Expression> variables, Scope<Expression> parameters, ErrorReporter errors) {
        if (value instanceof ReferenceTerm) {
            ReferenceTerm function = (ReferenceTerm) value;
            Expression variable = function.evaluate(variables, parameters, errors);
            if (variable == null) {
                errors.SemErr("missing value: " + function.toString());
                return;
            }
            if (variable.getTerms().size() > 1) {
                errors.SemErr("not a single value: " + function.toString());
                return;
            }

            value = variable.getTerms().get(0);
        }
    }
}
