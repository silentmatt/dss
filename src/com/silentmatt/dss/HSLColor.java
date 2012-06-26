package com.silentmatt.dss;

import com.silentmatt.dss.term.FunctionTerm;
import com.silentmatt.dss.term.NumberTerm;

/**
 * An RGB Color
 * @author Matthew Crumley
 */
@Immutable
public final class HSLColor extends Color {
    private final int hue;
    private final double saturation, lightness, alpha;

    /**
     * Constructs a Color from red, green, and blue channels.
     * The alpha channel is defaulted to 255 (fully opaque).
     *
     * @param red The red channel, from [0, 1].
     * @param green The green channel, from [0, 1].
     * @param blue the blue channel, from [0, 1].
     */
    public HSLColor(int hue, double saturation, double lightness) {
        this.hue = ((hue % 360) + 360) % 360;
        this.saturation = clampFloat(saturation);
        this.lightness = clampFloat(lightness);
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
    public HSLColor(int hue, double saturation, double lightness, double alpha) {
        this.hue = ((hue % 360) + 360) % 360;
        this.saturation = clampFloat(saturation);
        this.lightness = clampFloat(lightness);
        this.alpha = clampFloat(alpha);
    }

    /**
     * Converts the color to an equivalent RGBFColor.
     *
     * @return The color in rgba(r%, g%, b%, a) format.
     */
    @Override
    public RGBFColor toRGBFColor() {
        double h = hue / 360.0;
        double m2;
        if (lightness <= 0.5) {
            m2 = lightness * (saturation + 1.0);
        }
        else {
            m2 = lightness + saturation - lightness * saturation;
        }
        double m1 = lightness * 2.0 - m2;
        double r = hueToRGB(m1, m2, h + 1.0/3.0);
        double g = hueToRGB(m1, m2, h);
        double b = hueToRGB(m1, m2, h - 1.0/3.0);
        return new RGBFColor(r, g, b, alpha);
    }

    private static double hueToRGB(double m1, double m2, double h) {
        if (h < 0) {
            h += 1;
        }
        if (h > 1) {
            h -= 1;
        }
        if (h * 6 < 1) {
            return m1 + (m2 - m1) * h * 6.0;
        }
        if (h * 2 < 1) {
            return m2;
        }
        if (h * 3 < 2) {
            return m1 + (m2 - m1) * (2.0/3.0 - h) * 6.0;
        }
        return m1;
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
     * Gets the color as hsla(...) format.
     *
     * @return the color in "hsla(h, s, l, a.aa)" format.
     */
    public String toHSLString() {
        if (areEquivalent(alpha, 1.0)) {
            return "hsl(" + hue + "," + floatFormat.format(saturation * 100.0) + "%," + floatFormat.format(lightness * 100.0) + "%)";
        }
        else if (areEquivalent(alpha, 0.0)) {
            return "hsla(" + hue + "," + floatFormat.format(saturation * 100.0) + "%," + floatFormat.format(lightness * 100.0) + "%,0)";
        }
        return "hsla(" + hue + "," + floatFormat.format(saturation * 100.0) + "%," + floatFormat.format(lightness * 100.0) + "%," + floatFormat.format(alpha) + ")";
    }

    /**
     * Gets the default string representation of the color.
     *
     * @return {@link #toHexString()} if the alpha channel is 255, otherwise,
     * {@link #toRGBAString()}.
     */
    @Override
    public String toString() {
        return toHSLString();
    }

    /**
     * Compares two colors for channel-wise equality.
     *
     * @param other The Color to compare to this one.
     *
     * @return true iff <var>other</var> is identical to this Color.
     */
    public boolean equals(HSLColor other) {
        return this.hue == other.hue && this.saturation == other.saturation && this.lightness == other.lightness && this.alpha == other.alpha;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return equals((HSLColor) obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.hue;
        hash = 67 * hash + (int)(this.saturation * 100.0);
        hash = 67 * hash + (int)(this.lightness * 100.0);
        hash = 67 * hash + (int)(this.alpha * 255.0);
        return hash;
    }

    @Override
    public HSLColor toHSLColor() {
        return this;
    }

    public int getHue() {
        return hue;
    }

    public double getSaturation() {
        return saturation;
    }

    public double getLightness() {
        return lightness;
    }

    @Override
    public double getAlpha() {
        return alpha;
    }

    @Override
    public FunctionTerm toTerm() {
        Expression.Builder expr = new Expression.Builder();
        NumberTerm h = new NumberTerm(hue);
        NumberTerm s = new NumberTerm(',', saturation * 100, Unit.Percent);
        NumberTerm l = new NumberTerm(',', lightness * 100, Unit.Percent);
        NumberTerm a = new NumberTerm(',', alpha, Unit.None);
        expr.addTerm(h)
            .addTerm(s)
            .addTerm(l)
            .addTerm(a);
        return new FunctionTerm("hsla", expr.build());
    }

    @Override
    public Color withAlpha(double a) {
        return new HSLColor(hue, saturation, lightness, a);
    }

    @Override
    public RGBIColor toRGBColor() {
        return toRGBFColor().toRGBColor();
    }

    @Override
    public HSLColor convertToType(Color c) {
        return c.toHSLColor();
    }
}
