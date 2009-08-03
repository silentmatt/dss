package com.silentmatt.dss.calc;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.term.CalculationTerm;
import com.silentmatt.dss.term.FunctionTerm;
import com.silentmatt.dss.term.NumberTerm;
import com.silentmatt.dss.term.ReferenceTerm;
import com.silentmatt.dss.term.Term;
import java.util.List;

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

    public Value calculateValue(EvaluationState state) {
        substituteValues(state);
        if (value instanceof NumberTerm) {
            return new Value((NumberTerm) value);
        }
        else if (value instanceof CalculationTerm) {
            return ((CalculationTerm) value).getCalculation().calculateValue(state);
        }
        else if (value instanceof FunctionTerm) {
            Expression result = ((FunctionTerm) value).applyFunction(state);
            if (result != null) {
                List<Term> resultTerms = result.getTerms();
                if (resultTerms.size() == 1 && resultTerms.get(0) instanceof NumberTerm) {
                    return new Value((NumberTerm) resultTerms.get(0));
                }
            }
        }

        state.getErrors().SemErr("Invalid term in calculation: '" + value + "'");
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

    public void substituteValues(EvaluationState state) {
        if (value instanceof ReferenceTerm) {
            ReferenceTerm function = (ReferenceTerm) value;
            Expression variable = function.evaluate(state);
            if (variable == null) {
                state.getErrors().SemErr("missing value: " + function.toString());
                return;
            }
            if (variable.getTerms().size() > 1) {
                state.getErrors().SemErr("not a single value: " + function.toString());
                return;
            }

            value = variable.getTerms().get(0);
        }
    }
}