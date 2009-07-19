package com.silentmatt.dss.expression;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Function;
import com.silentmatt.dss.Scope;
import com.silentmatt.dss.Term;

/**
 *
 * @author matt
 */
public class TermExpression implements CalcExpression {
    private Term value;

    public TermExpression(Term value) {
        this.value = value;
    }

    public Value calculateValue(Scope<Expression> variables, Scope<Expression> parameters) throws CalculationException {
        Value val;

        switch (value.getType()) {
        case Number:
            val = new Value(value);
            break;
        case Function:
            {
                Function fn = value.getFunction();
                Expression variable = null;
                if (fn.getName().equals("const")) {
                    variable = variables.get(fn.getExpression().toString());
                }
                else if (parameters != null && fn.getName().equals("param")) {
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

                Term term = variable.getTerms().get(0);
                val = new Value(term);
            }
            break;
        case ClassReference:
        case Hex:
        case String:
        case Unicode:
        case Url:
        default:
            throw new CalculationException("Invalid term type: " + value.getType());
        }

        return val;
    }

    public int getPrecidence() {
        return 3;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}
