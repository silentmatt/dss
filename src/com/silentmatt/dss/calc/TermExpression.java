package com.silentmatt.dss.calc;

import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.term.CalculationTerm;
import com.silentmatt.dss.term.FunctionTerm;
import com.silentmatt.dss.term.NumberTerm;
import com.silentmatt.dss.term.ParamTerm;
import com.silentmatt.dss.term.ReferenceTerm;
import com.silentmatt.dss.term.Term;
import java.util.List;

/**
 * A CalcExpression that evaluates to a simple DSS {@link Term}.
 *
 * @author Matthew Crumley
 */
@Immutable
public class TermExpression implements CalcExpression {
    private final Term value;

    /**
     * Constructs a TermExpression from a DSS Term.
     *
     * @param value The Term this expression will return.
     */
    public TermExpression(Term value) {
        this.value = value;
    }

    public Value calculateValue(EvaluationState state, DeclarationList container) {
        Term value = withSubstitutedValues(state, container, true, false).value;
        if (value instanceof NumberTerm) {
            return new Value((NumberTerm) value);
        }
        else if (value instanceof CalculationTerm) {
            return ((CalculationTerm) value).getCalculation().calculateValue(state, container);
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
        return 4;
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

    public TermExpression withSubstitutedValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doNestedCalculations) {
        if (value instanceof ReferenceTerm && !(!withParams && value instanceof ParamTerm)) {
            ReferenceTerm function = (ReferenceTerm) value;
            Expression variable = function.evaluate(state, container);
            if (variable == null) {
                state.getErrors().SemErr("missing value: " + function.toString());
                return this;
            }
            if (variable.getTerms().size() > 1) {
                state.getErrors().SemErr("not a single value: " + function.toString());
                return this;
            }

            return new TermExpression(variable.getTerms().get(0));
        }
        else if (doNestedCalculations && value instanceof CalculationTerm) {
            Value calc = calculateValue(state, container);
            if (calc != null) {
                try {
                    return new TermExpression(calc.toTerm());
                } catch (CalculationException ex) {
                    state.getErrors().SemErr(ex.getMessage());
                    return null;
                }
            }
            return this;
        }
        else {
            return this;
        }
    }
}
