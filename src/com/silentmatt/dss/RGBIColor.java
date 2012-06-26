package com.silentmatt.dss;

import com.silentmatt.dss.term.FunctionTerm;
import com.silentmatt.dss.term.HexTerm;
import com.silentmatt.dss.term.NumberTerm;
import com.silentmatt.dss.term.Term;

/**
 * An RGB Color
 * @author Matthew Crumley
 */
@Immutable
public final class RGBIColor extends Color {
    private final int red, green, blue;
    private final double alpha;
    private final String name;

    /**
     * Constructs a Color from red, green, and blue channels.
     * The alpha channel is defaulted to 255 (fully opaque).
     *
     * @param red The red channel, from [0, 255].
     * @param green The green channel, from [0, 255].
     * @param blue the blue channel, from [0, 255].
     */
    public RGBIColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = 1.0;
        this.name = null;
    }

    protected RGBIColor(int red, int green, int blue, String name) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = 1.0;
        this.name = name.toLowerCase();
    }

    /**
     * Constructs a Color from red, green, blue, and alpha channels.
     *
     * @param red The red channel, from [0, 255].
     * @param green The green channel, from [0, 255].
     * @param blue The blue channel, from [0, 255].
     * @param alpha The alpha channel, from [0, 255].
     */
    public RGBIColor(int red, int green, int blue, double alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.name = null;
    }

    /**
     * Gets the color in CSS hexidecimal format. The alpha channel is ignored.
     *
     * @return The color in "#RRGGBB" format.
     */
    public String toHexString() {
        if ((red & 0x0F) == ((red >> 4) & 0x0F) &&
            (green & 0x0F) == ((green >> 4) & 0x0F) &&
            (blue & 0x0F) == ((blue >> 4) & 0x0F)) {
            return String.format("#%X%X%X", red & 0x0F, green & 0x0F, blue & 0x0F);
        }
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    /**
     * Gets the color as rgba(...) format.
     *
     * @return the color in "rgba(r, g, b, a.aa)" format.
     */
    public String toRGBString() {
        if (alpha == 1.0) {
            return "rgb(" + red + ", " + green + ", " + blue + ")";
        }
        return "rgba(" + red + ", " + green + ", " + blue + ", " + alpha + ")";
    }

    /**
     * Gets the color name if it exists, or the default representation.
     *
     * @return A CSS color name, or {@link #toString()}.
     */
    public String toNameString() {
        if (name != null) {
            return name;
        }
        String thisString = toString();
        for (Color c : namedColors.values()) {
            if (this.equals(c)) {
                return c.toNameString();
            }
        }
        return thisString;
    }

    /**
     * Gets the default string representation of the color.
     *
     * @return {@link #toHexString()} if the alpha channel is 255, otherwise,
     * {@link #toRGBAString()}.
     */
    @Override
    public String toString() {
        return alpha == 1.0 ? toHexString() : toRGBString();
    }

    /**
     * Compares two colors for channel-wise equality.
     *
     * @param other The Color to compare to this one.
     *
     * @return true iff <var>other</var> is identical to this Color.
     */
    public boolean equals(RGBIColor other) {
        return this.red == other.red && this.green == other.green && this.blue == other.blue && this.alpha == other.alpha;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return equals((RGBIColor) obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.red;
        hash = 67 * hash + this.green;
        hash = 67 * hash + this.blue;
        hash = 67 * hash + (int)(this.alpha * 255.0);
        return hash;
    }

    @Override
    public RGBFColor toRGBFColor() {
        return new RGBFColor(red / 255.0, green / 255.0, blue / 255.0, alpha);
    }

    @Override
    public HSLColor toHSLColor() {
        return toRGBFColor().toHSLColor();
    }

    @Override
    public RGBIColor toRGBColor() {
        return toRGBFColor().toRGBColor();
    }

    public double getAlpha() {
        return alpha;
    }

    @Override
    public Color withAlpha(double a) {
        return new RGBIColor(red, green, blue, a);
    }

    @Override
    public Term toTerm() {
        if (alpha != 1.0) {
            Expression.Builder expr = new Expression.Builder();
            NumberTerm r = new NumberTerm(red);
            NumberTerm g = new NumberTerm(green); g = g.withSeparator(',');
            NumberTerm b = new NumberTerm(blue);  b = b.withSeparator(',');
            NumberTerm a = new NumberTerm(alpha); a = a.withSeparator(',');
            expr.addTerm(r)
                .addTerm(g)
                .addTerm(b)
                .addTerm(a);
            return new FunctionTerm("rgba", expr.build());
        }
        else {
            return new HexTerm(toHexString());
        }
    }
}
