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

    /**
     * Default constructor.
     */
    public FunctionTerm() {
        super();
    }

    /**
     * Constructs a FunctionTerm with a default name and parameters.
     *
     * @param name The function name
     * @param expression The parameters
     */
    public FunctionTerm(String name, Expression expression) {
        super();
        this.name = name;
        this.expression = expression;
    }

    public FunctionTerm clone() {
        FunctionTerm result = new FunctionTerm(name, expression.clone());
        result.setSeperator(getSeperator());
        return result;
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
     * @return A String of the form "name(expression)".
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

    /**
     * Calls a program-defined function and returns the result.
     *
     * @param state The DSS evaluation state, containing the "functions" scope.
     * @return The result of calling the function, or null if there's an error
     *         or the function doesn't exist.
     */
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

    /**
     * Converts the term to a Color object.
     * Functions that can be colors are rgb, rgba, hsl, and hsla.
     * The "non-alpha" versions are equivalent to the "alpha" versions. For
     * example, <code>rgb(255,0,0,128)</code> works, and sets the alpha channel
     * to 128.
     *
     * You can also mix percentages and integer values. Each channel is
     * considered separately.
     *
     * @return The Color corresponding to this function, or null.
     */
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
                case 3: fa = getAlphaValue((NumberTerm) term); break;
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
                case 3: a = getAlphaValue((NumberTerm) term); break;
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

    /**
     * Converts a numeric term to a color channel.
     * Percentages are scaled from 0 to 255, otherwise, the scalar part is
     * converted to an integer.
     *
     * @param t The numberTerm to convert
     * @return The RGBA channel value
     */
    private static int getRGBValue(NumberTerm t) {
        if (t.getUnit() == Unit.Percent) {
            return (int)(255.0 * t.getValue() / 100.0);
        }
        return (int) t.getValue();
    }

    /**
     * Converts a numeric term to a hue.
     *
     * @param t The NumberTerm to convert
     * @return A hue value from 0 to 255 (as long as t is from 0 to 360)
     */
    private static int getHueValue(NumberTerm t) {
        return (int)(t.getValue() * 255.0 / 360.0);
    }

    /**
     * Converts a numeric term to a hue.
     *
     * @param t The NumberTerm to convert
     * @return A hue value from 0 to 255 (as long as t is from 0 to 360)
     */
    private static int getAlphaValue(NumberTerm t) {
        return (int)(t.getValue() * 255.0);
    }

    /**
     * Performs any substitution in the arguments and calls the function if it
     * exists.
     *
     * @param state The current DSS evaluation state
     * @param withParams <code>true</code> if <code>ParamTerm</code>s should be
     *                   evaluated
     * @param doCalculations <code>true</code> if <code>CalcTerm</code>s should
     *                       be evaluated
     * @return The result of the defined function, or <code>toExpression()</code>
     *         if there is no program-defined function.
     */
    @Override
    public Expression substituteValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        Expression argument = getExpression().substituteValues(state, container, withParams, doCalculations);
        Expression result = new FunctionTerm(getName(), argument).applyFunction(state);
        if (result == null) {
            result = new FunctionTerm(getName(), argument).toExpression();
        }
        result.getTerms().get(0).setSeperator(getSeperator());
        return result;
    }
}
