package com.silentmatt.dss.term;

import com.silentmatt.dss.Color;
import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Function;
import com.silentmatt.dss.Unit;

/**
 * A function "call" term.
 *
 * "Functions" include rgb(...), but <strong>not</strong> url(...), const(...), param(...), or calc(...).
 *
 * @author Matthew Crumley
 */
public class FunctionTerm extends Term {
    /**
     * The function name.
     */
    private String name;

    /**
     * The parameters to the function.
     */
    private Expression expression;

    public FunctionTerm() {
        super();
    }

    public FunctionTerm(String name, Expression expression) {
        super();
        this.name = name;
        this.expression = expression;
    }

    /**
     * Gets the name of the function.
     *
     * @return The referenced function's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the function.
     *
     * @param Name The referenced function's name.
     */
    public void setName(String Name) {
        this.name = Name;
    }

    /**
     * Gets the expression that is passed to the function.
     *
     * @return The function parameter expression.
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Sets the expression that is passed to the function.
     *
     * @param Expression The function parameter expression.
     */
    public void setExpression(Expression Expression) {
        this.expression = Expression;
    }

    /**
     * Gets the function term as a String.
     *
     * @return A String of the form "function(expression)".
     */
    @Override
    public String toString() {
        if (isColor()) {
            return toColor().toString();
        }
        StringBuilder txt = new StringBuilder();
        txt.append(name).append("(");
        if (expression != null) {
            txt.append(expression.toString());
        }
        txt.append(")");
        return txt.toString();
    }

    public Expression applyFunction(EvaluationState state) {
        Function function = state.getFunctions().get(getName());
        if (function != null) {
            try {
                return function.call(this, state);
            }
            catch (Exception ex) {
                state.getErrors().Warning(ex.getMessage());
            }
        }
        return null;
    }

    @Override
    public boolean isColor() {
        return toColor() != null;
    }

    @Override
    public Color toColor() {
        if ((name.equalsIgnoreCase("rgb") && expression.getTerms().size() == 3)
            || (name.equalsIgnoreCase("rgba") && expression.getTerms().size() == 4)
            ) {
            int fr = 0, fg = 0, fb = 0, fa = 255;
            for (int i = 0; i < expression.getTerms().size(); i++) {
                Term term = expression.getTerms().get(i);
                if (!(term instanceof NumberTerm) ||
                        (i > 0 && (term.getSeperator() == null || !term.getSeperator().equals(','))) ) {
                    return null;
                }
                switch (i) {
                case 0: fr = getRGBValue((NumberTerm) term); break;
                case 1: fg = getRGBValue((NumberTerm) term); break;
                case 2: fb = getRGBValue((NumberTerm) term); break;
                case 3: fa = getRGBValue((NumberTerm) term); break;
                default: break;
                }
            }
            return new Color(fr, fg, fb, fa);
        } else if ((name.equalsIgnoreCase("hsl") && expression.getTerms().size() == 3)
            || (name.equalsIgnoreCase("hsla") && expression.getTerms().size() == 4)
            ) {
            int h = 0, s = 0, v = 0, a = 255;
            for (int i = 0; i < expression.getTerms().size(); i++) {
                Term term = expression.getTerms().get(i);
                if (!(term instanceof NumberTerm) || term.getSeperator() == null || !term.getSeperator().equals(',') ) {
                    return null;
                }
                switch (i) {
                case 0: h = getHueValue((NumberTerm) term); break;
                case 1: s = getRGBValue((NumberTerm) term); break;
                case 2: v = getRGBValue((NumberTerm) term); break;
                case 3: a = getRGBValue((NumberTerm) term); break;
                default: break;
                }
            }
            java.awt.Color jColor = java.awt.Color.getHSBColor(h, s, v);
            return new Color(jColor.getRed(), jColor.getGreen(), jColor.getBlue(), a);
        }
        else {
            return null;
        }
    }

    private static int getRGBValue(NumberTerm t) {
        if (t.getUnit() == Unit.Percent) {
            return (int)(255.0 * t.getDoubleValue() / 100.0);
        }
        return (int) t.getDoubleValue();
    }

    private static int getHueValue(NumberTerm t) {
        return (int)(t.getDoubleValue() * 255.0 / 360.0);
    }

    @Override
    public Expression substituteValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        Expression argument = getExpression().substituteValues(state, container, withParams, doCalculations);
        Expression result = new FunctionTerm(getName(), argument).applyFunction(state);
        return result != null ? result : toExpression();
    }
}
