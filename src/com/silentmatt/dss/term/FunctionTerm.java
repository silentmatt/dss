package com.silentmatt.dss.term;

import com.silentmatt.dss.Color;
import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Function;
import com.silentmatt.dss.HSLColor;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.RGBFColor;
import com.silentmatt.dss.RGBIColor;
import com.silentmatt.dss.Unit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A function "call" term.
 *
 * "Functions" include rgb(...), but <strong>not</strong> url(...), const(...), param(...), or calc(...).
 *
 * @author Matthew Crumley
 */
@Immutable
public final class FunctionTerm extends Term {
    /**
     * The function name.
     */
    private final String name;

    /**
     * The parameters to the function.
     */
    private final Expression expression;

    /**
     * Constructs a FunctionTerm with a default name and parameters.
     *
     * @param name The function name
     * @param expression The parameters
     */
    public FunctionTerm(String name, Expression expression) {
        super(null);
        this.name = name;
        this.expression = expression;
    }

    /**
     * Constructs a FunctionTerm with a default name, separator, and parameters.
     *
     * @param sep The separator
     * @param name The function name
     * @param expression The parameters
     */
    public FunctionTerm(Character separator, String name, Expression expression) {
        super(separator);
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
     * Gets the expression that is passed to the function.
     *
     * @return The function parameter expression.
     */
    public Expression getExpression() {
        return expression;
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
        if (function == null) {
            function = builtinFunctions.get(getName());
        }
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

    private boolean isRGBIColor() {
        try {
            List<Term> channels = expression.getTerms();
            if (!(name.equalsIgnoreCase("rgb") || name.equalsIgnoreCase("rgba"))) {
                return false;
            }
            if (!(channels.size() == 3 || channels.size() == 4)) {
                return false;
            }
            if (((NumberTerm) channels.get(0)).getUnit() != Unit.None) {
                return false;
            }
            if (((NumberTerm) channels.get(1)).getUnit() != Unit.None) {
                return false;
            }
            if (((NumberTerm) channels.get(2)).getUnit() != Unit.None) {
                return false;
            }
            if (channels.size() == 4 && ((NumberTerm) channels.get(3)).getUnit() != Unit.None) {
                return false;
            }
            return true;
        }
        catch (ClassCastException ex) {
            return false;
        }
    }

    private boolean isRGBFColor() {
        try {
            List<Term> channels = expression.getTerms();
            if (!(name.equalsIgnoreCase("rgb") || name.equalsIgnoreCase("rgba"))) {
                return false;
            }
            if (!(channels.size() == 3 || channels.size() == 4)) {
                return false;
            }
            if (((NumberTerm) channels.get(0)).getUnit() != Unit.Percent) {
                return false;
            }
            if (((NumberTerm) channels.get(1)).getUnit() != Unit.Percent) {
                return false;
            }
            if (((NumberTerm) channels.get(2)).getUnit() != Unit.Percent) {
                return false;
            }
            if (channels.size() == 4 && ((NumberTerm) channels.get(3)).getUnit() != Unit.None) {
                return false;
            }
            return true;
        }
        catch (ClassCastException ex) {
            return false;
        }
    }

    private boolean isHSLColor() {
        try {
            List<Term> channels = expression.getTerms();
            if (!(name.equalsIgnoreCase("hsl") || name.equalsIgnoreCase("hsla"))) {
                return false;
            }
            if (!(channels.size() == 3 || channels.size() == 4)) {
                return false;
            }
            if (((NumberTerm) channels.get(0)).getUnit() != Unit.None) {
                return false;
            }
            if (((NumberTerm) channels.get(1)).getUnit() != Unit.Percent) {
                return false;
            }
            if (((NumberTerm) channels.get(2)).getUnit() != Unit.Percent) {
                return false;
            }
            if (channels.size() == 4 && ((NumberTerm) channels.get(3)).getUnit() != Unit.None) {
                return false;
            }
            return true;
        }
        catch (ClassCastException ex) {
            return false;
        }
    }

    /**
     * Converts the term to a Color object.
     * Functions that can be colors are rgb, rgba, hsl, and hsla.
     * The "non-alpha" versions are equivalent to the "alpha" versions. For
     * example, <code>rgb(255,0,0,0.5)</code> works, and sets the alpha channel
     * to 0.5.
     *
     * You can also mix percentages and integer values. Each channel is
     * considered separately.
     *
     * @return The Color corresponding to this function, or null.
     */
    @Override
    public Color toColor() {
        List<Term> terms = expression.getTerms();
        double a = 1.0;

        if (terms.size() == 4 && terms.get(3) instanceof NumberTerm) {
            a = ((NumberTerm) terms.get(3)).getValue();
        }

        if (this.isRGBIColor()) {
            int r = (int) ((NumberTerm) terms.get(0)).getValue();
            int g = (int) ((NumberTerm) terms.get(1)).getValue();
            int b = (int) ((NumberTerm) terms.get(2)).getValue();
            
            return new RGBIColor(r, g, b, a);
        }
        else if (this.isRGBFColor()) {
            double r = ((NumberTerm) terms.get(0)).getValue() / 100.0;
            double g = ((NumberTerm) terms.get(1)).getValue() / 100.0;
            double b = ((NumberTerm) terms.get(2)).getValue() / 100.0;

            return new RGBFColor(r, g, b, a);
        }
        else if (this.isHSLColor()) {
            int h = (int) ((NumberTerm) terms.get(0)).getValue();
            double s = ((NumberTerm) terms.get(1)).getValue() / 100.0;
            double l = ((NumberTerm) terms.get(2)).getValue() / 100.0;

            return new HSLColor(h, s, l, a);
        }
        else {
            return null;
        }
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
        //result.getTerms().get(0).setSeperator(getSeperator());
        Expression.Builder resultBuilder = new Expression.Builder();
        boolean first = true;
        for (Term t : result.getTerms()) {
            if (first) {
                resultBuilder.addTerm(t.withSeparator(getSeperator()));
                first = false;
            }
            else {
                resultBuilder.addTerm(t);
            }
        }
        return resultBuilder.build();
    }

    private static final Map<String, Function> builtinFunctions = new HashMap<String, Function>();
    static {
        Function hueshift = new HueShift();
        builtinFunctions.put("compose", new ComposeFunction());
        builtinFunctions.put("whiten", new OverlayFunction(Color.White));
        builtinFunctions.put("blacken", new OverlayFunction(Color.Black));
        builtinFunctions.put("lighten", new Lighten());
        builtinFunctions.put("darken", new Darken());
        builtinFunctions.put("saturate", new Saturate());
        builtinFunctions.put("desaturate", new Desaturate());
        builtinFunctions.put("hueshift", hueshift);
        builtinFunctions.put("spin", hueshift);
        builtinFunctions.put("fadein", new FadeIn());
        builtinFunctions.put("fadeout", new FadeOut());

        builtinFunctions.put("toHSL", new Function() {
            @Override
            public Expression call(FunctionTerm function, EvaluationState state) {
                List<Term> args = function.getExpression().getTerms();
                if (!(args.size() == 1 && args.get(0).isColor())) {
                    return function.toExpression();
                }
                return args.get(0).toColor().toHSLColor().toTerm().toExpression();
            }
        });
        builtinFunctions.put("toRGB", new Function() {
            @Override
            public Expression call(FunctionTerm function, EvaluationState state) {
                List<Term> args = function.getExpression().getTerms();
                if (!(args.size() == 1 && args.get(0).isColor())) {
                    return function.toExpression();
                }
                return args.get(0).toColor().toRGBColor().toTerm().toExpression();
            }
        });
        builtinFunctions.put("alpha", new Function() {
            @Override
            public Expression call(FunctionTerm function, EvaluationState state) {
                List<Term> args = function.getExpression().getTerms();
                if (!(args.size() == 2 && args.get(0).isColor() && args.get(1) instanceof NumberTerm)) {
                    return function.toExpression();
                }
                double alpha = ((NumberTerm) args.get(1)).getValue();
                return args.get(0).toColor().withAlpha(alpha).toTerm().toExpression();
            }
        });
    }

    @Override
    public FunctionTerm withSeparator(Character separator) {
        return new FunctionTerm(separator, getName(), getExpression());
    }

    private static class ComposeFunction implements Function {
        public RGBFColor call(Color foregroud, Color background, double alpha) {
            RGBFColor f = foregroud.toRGBFColor();
            RGBFColor b = background.toRGBFColor();

            double af = f.getAlpha() * alpha;
            double ab = b.getAlpha();
            double inv_af = 1.0 - af;
            double ap = af + inv_af * ab;
            double rp = (f.getRed()   * af + inv_af * b.getRed()   * ab) / ap;
            double gp = (f.getGreen() * af + inv_af * b.getGreen() * ab) / ap;
            double bp = (f.getBlue()  * af + inv_af * b.getBlue()  * ab) / ap;

            return new RGBFColor(rp, gp, bp, ap);
        }

        @Override
        public Expression call(FunctionTerm function, EvaluationState state) {
            List<Term> params = function.getExpression().getTerms();
            if (!(params.size() == 2 || params.size() == 3)) {
                return function.toExpression();
            }
            Term bottom = params.get(0);
            Term top = params.get(1);
            Term alpha = null;
            if (params.size() == 3) {
                alpha = params.get(2);
            }

            if (!(bottom.isColor() && top.isColor() && (alpha == null || alpha instanceof NumberTerm))) {
                return function.toExpression();
            }

            Color f = top.toColor();
            Color b = bottom.toColor();
            double a = 0.5;
            if (alpha != null) {
                a = (double)((NumberTerm) alpha).getValue();
                if (((NumberTerm) alpha).getUnit() == Unit.Percent) {
                    a /= 100.0;
                }
            }

            Color result = call(f, b, a);
            if (f.getClass() == b.getClass()) {
                result = f.convertToType(result);
            }
            return result.toTerm().toExpression();
        }
    }

    private static class OverlayFunction extends ComposeFunction {
        private final Color toOverlay;

        public OverlayFunction(Color toOverlay) {
            this.toOverlay = toOverlay;
        }

        @Override
        public Expression call(FunctionTerm function, EvaluationState state) {
            List<Term> params = function.getExpression().getTerms();
            if (!(params.size() == 1 || params.size() == 2)) {
                return function.toExpression();
            }
            Term color = params.get(0);
            Term amount = null;

            if (params.size() == 2) {
                amount = params.get(1);
            }

            if (!(color.isColor() && (amount == null || amount instanceof NumberTerm))) {
                return function.toExpression();
            }

            double a = 0.1;
            if (amount != null) {
                a = ((NumberTerm) amount).getValue();
                if (((NumberTerm) amount).getUnit() == Unit.Percent) {
                    a /= 100.0;
                }
            }

            return color.toColor().convertToType(call(toOverlay, color.toColor(), a)).toTerm().toExpression();
        }

    }

    private static abstract class ColorScalarFunction implements Function {
        protected double defaultValue, percentAdjust;

        protected ColorScalarFunction(double defaultValue, double percentAdjust) {
            this.defaultValue = defaultValue;
            this.percentAdjust = percentAdjust;
        }

        protected ColorScalarFunction() {
            this(0.1, 1.0);
        }

        protected abstract Color calculate(Color c, double scalar);

        protected double toScalar(NumberTerm a) {
            double scalar;
            if (a != null && (a.getUnit() == Unit.None || a.getUnit() == Unit.Percent)) {
                scalar = a.getValue();
                if (a.getUnit() == Unit.Percent) {
                    scalar = (scalar / 100.0) * percentAdjust;
                }
            }
            else {
                scalar = defaultValue;
            }

            return scalar;
        }

        @Override
        public Expression call(FunctionTerm function, EvaluationState state) {
            List<Term> params = function.getExpression().getTerms();
            if (!(params.size() == 1 || params.size() == 2)) {
                return function.toExpression();
            }
            Term color = params.get(0);
            Term amount = null;

            if (params.size() == 2) {
                amount = params.get(1);
            }

            if (!color.isColor()) {
                state.getErrors().Warning("Not a color: " + color);
                return function.toExpression();
            }
            if (!(amount == null || amount instanceof NumberTerm)) {
                state.getErrors().Warning("Invalid value for " + function.getName() + ": " + amount);
                return function.toExpression();
            }

            //HSLColor c = color.toColor().toHSLColor();
            return color.toColor().convertToType(calculate(color.toColor(), toScalar((NumberTerm) amount))).toTerm().toExpression();
        }
    }

    private static class Lighten extends ColorScalarFunction {
        @Override
        protected Color calculate(Color c, double scalar) {
            HSLColor hsl = c.toHSLColor();
            return new HSLColor(hsl.getHue(), hsl.getSaturation(), hsl.getLightness() + scalar, hsl.getAlpha());
        }
    }

    private static class Darken extends ColorScalarFunction {
        @Override
        protected Color calculate(Color c, double scalar) {
            HSLColor hsl = c.toHSLColor();
            return new HSLColor(hsl.getHue(), hsl.getSaturation(), hsl.getLightness() - scalar, hsl.getAlpha());
        }
    }

    private static class Saturate extends ColorScalarFunction {
        @Override
        protected Color calculate(Color c, double scalar) {
            HSLColor hsl = c.toHSLColor();
            return new HSLColor(hsl.getHue(), hsl.getSaturation() + scalar, hsl.getLightness(), hsl.getAlpha());
        }
    }

    private static class Desaturate extends ColorScalarFunction {
        @Override
        protected Color calculate(Color c, double scalar) {
            HSLColor hsl = c.toHSLColor();
            return new HSLColor(hsl.getHue(), hsl.getSaturation() - scalar, hsl.getLightness(), hsl.getAlpha());
        }
    }

    private static class HueShift extends ColorScalarFunction {
        public HueShift() {
            super(30.0, 360.0);
        }

        @Override
        protected Color calculate(Color c, double scalar) {
            HSLColor hsl = c.toHSLColor();
            return new HSLColor(hsl.getHue() + (int)(Math.round(scalar) % 360), hsl.getSaturation(), hsl.getLightness(), hsl.getAlpha());
        }
    }

    private static class FadeIn extends ColorScalarFunction {
        @Override
        protected Color calculate(Color c, double scalar) {
            return c.withAlpha(c.getAlpha() + scalar);
        }
    }

    private static class FadeOut extends ColorScalarFunction {
        @Override
        protected Color calculate(Color c, double scalar) {
            return c.withAlpha(c.getAlpha()- scalar);
        }
    }

/*    private static class BrightnessFunction implements Function {
        private boolean inverse;

        public BrightnessFunction(boolean inverse) {
            this.inverse = inverse;
        }

        @Override
        public Expression call(FunctionTerm function, EvaluationState state) {
            List<Term> params = function.getExpression().getTerms();
            if (!(params.size() == 1 || params.size() == 2)) {
                return function.toExpression();
            }
            Term color = params.get(0);
            Term amount = null;

            if (params.size() == 2) {
                amount = params.get(1);
            }

            if (!(color.isColor() && (amount == null || amount instanceof NumberTerm))) {
                return function.toExpression();
            }

            double a = 0.1;
            if (amount != null) {
                a = ((NumberTerm) amount).getValue();
                if (((NumberTerm) amount).getUnit() == Unit.Percent) {
                    a /= 100.0;
                }
            }

            if (inverse) {
                a = -a;
            }

            HSLColor c = color.toColor().toHSLColor();
            return new HSLColor(c.getHue(), c.getSaturation(), c.getLightness() + a, c.getAlpha()).toTerm().toExpression();
        }
    }
*/
}
