package com.silentmatt.dss;

import com.silentmatt.dss.term.FunctionTerm;
import com.silentmatt.dss.term.NumberTerm;

/**
 * An RGB Color
 * @author Matthew Crumley
 */
@Immutable
public final class RGBFColor extends Color {
    private final double red, green, blue, alpha;

    /**
     * Constructs a Color from red, green, and blue channels.
     * The alpha channel is defaulted to 255 (fully opaque).
     *
     * @param red The red channel, from [0, 1].
     * @param green The green channel, from [0, 1].
     * @param blue the blue channel, from [0, 1].
     */
    public RGBFColor(double red, double green, double blue) {
        this.red = clampFloat(red);
        this.green = clampFloat(green);
        this.blue = clampFloat(blue);
        this.alpha = 1.0;
    }

    /**
     * Constructs a Color from red, green, blue, and alpha channels.
     *
     * @param red The red channel, from [0, 1].
     * @param green The green channel, from [0, 1].
     * @param blue The blue channel, from [0, 1].
     * @param alpha The alpha channel, from [0, 1].
     */
    public RGBFColor(double red, double green, double blue, double alpha) {
        this.red = clampFloat(red);
        this.green = clampFloat(green);
        this.blue = clampFloat(blue);
        this.alpha = alpha;
    }

    /**
     * Gets the color as rgba(...) format.
     *
     * @return the color in "rgba(r%, g%, b%, a.aa)" format.
     */
    public String toRGBString() {
        if (areEquivalent(alpha, 1.0)) {
            return "rgb(" + floatFormat.format(red * 100.0) + "%," + floatFormat.format(green * 100.0) + "%," + floatFormat.format(blue * 100.0) + "%)";
        }
        else if (areEquivalent(alpha, 0.0)) {
            return "rgba(" + floatFormat.format(red * 100.0) + "%," + floatFormat.format(green * 100.0) + "%," + floatFormat.format(blue * 100.0) + "%,0)";
        }
        return "rgba(" + floatFormat.format(red * 100.0) + "%," + floatFormat.format(green * 100.0) + "%," + floatFormat.format(blue * 100.0) + "%," + floatFormat.format(alpha) + ")";
    }

    /**
     * Gets the color name if it exists, or the default representation.
     *
     * @return A CSS color name, or {@link #toString()}.
     */
    @Override
    public String toNameString() {
        // TODO: Maybe implement this?
        return toString();
    }

    /**
     * Gets the default string representation of the color.
     *
     * @return {@link #toHexString()} if the alpha channel is 255, otherwise,
     * {@link #toRGBAString()}.
     */
    @Override
    public String toString() {
        return toRGBString();
    }

    /**
     * Compares two colors for channel-wise equality.
     *
     * @param other The Color to compare to this one.
     *
     * @return true iff <var>other</var> is identical to this Color.
     */
    public boolean equals(RGBFColor other) {
        return this.red == other.red && this.green == other.green && this.blue == other.blue && this.alpha == other.alpha;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return equals((RGBFColor) obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (int)(this.red * 255.0);
        hash = 67 * hash + (int)(this.green * 255.0);
        hash = 67 * hash + (int)(this.blue * 255.0);
        hash = 67 * hash + (int)(this.alpha * 255.0);
        return hash;
    }

    @Override
    public RGBFColor toRGBFColor() {
        return this;
    }

    public double getRed() {
        return this.red;
    }

    public double getGreen() {
        return this.green;
    }

    public double getBlue() {
        return this.blue;
    }

    @Override
    public double getAlpha() {
        return this.alpha;
    }

    @Override
    public FunctionTerm toTerm() {
        Expression.Builder expr = new Expression.Builder();
        NumberTerm r = new NumberTerm(null, red * 100, Unit.Percent);
        NumberTerm g = new NumberTerm(',', green * 100, Unit.Percent);
        NumberTerm b = new NumberTerm(',', blue * 100, Unit.Percent);
        NumberTerm a = new NumberTerm(',', alpha, Unit.None);
        expr.addTerm(r)
            .addTerm(g)
            .addTerm(b)
            .addTerm(a);
        return new FunctionTerm("rgba", expr.build());
    }

    @Override
    public HSLColor toHSLColor() {
        double M = Math.max(Math.max(red, green), blue);
        double m = Math.min(Math.min(red, green), blue);
        double C = M - m;

        double Hp;
        if (C == 0) {
            Hp = 0;
        }
        else if(M == red) {
            Hp = (green - blue) / C;
        }
        else if (M == green) {
            Hp = ((blue - red) / C) + 2;
        }
        else { // M == blue
            Hp = ((red - green) / C) + 4;
        }

        int H = (int)(Hp * 60.0);
        double L = 0.5 * (M + m);

        double S;
        if (C == 0) {
            S = 0;
        }
        else {
            S = C / (1.0 - Math.abs(2.0 * L - 1.0));
        }

        return new HSLColor(H, S, L, alpha);
     }

    @Override
    public Color withAlpha(double a) {
        return new RGBFColor(red, green, blue, a);
    }

    @Override
    public RGBIColor toRGBColor() {
        return new RGBIColor((int)Math.round(red * 255), (int)Math.round(green * 255), (int)Math.round(blue * 255), alpha);
    }

    @Override
    public RGBFColor convertToType(Color c) {
        return c.toRGBFColor();
    }
}
