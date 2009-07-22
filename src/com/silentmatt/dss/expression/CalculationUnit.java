package com.silentmatt.dss.expression;

import com.silentmatt.dss.Unit;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a CSS unit that can be used in calculations.
 * @author Matthew Crumley
 */
public class CalculationUnit implements Comparable<CalculationUnit> {
    /**
     * a scalar (dimensionless) unit
     */
    public static CalculationUnit None = new CalculationUnit(1, 0,0,0,0,0,0, Unit.None);

    /**
     * pixels
     */
    public static CalculationUnit PX = new CalculationUnit( 1, 1,0,0,0,0,0, Unit.PX);

    /**
     * percentage
     */
    public static CalculationUnit Percent = new CalculationUnit( 1, 0,1,0,0,0,0, Unit.Percent);

    /**
     * inches
     */
    public static CalculationUnit IN = new CalculationUnit(            72, 0,0,1,0,0,0, Unit.IN);
    /**
     * millimeters
     */
    public static CalculationUnit MM = new CalculationUnit(.0393700787*72, 0,0,1,0,0,0, Unit.MM);
    /**
     * centimeters
     */
    public static CalculationUnit CM = new CalculationUnit(0.393700787*72, 0,0,1,0,0,0, Unit.CM);
    /**
     * points (1/72 of an inch)
     */
    public static CalculationUnit PT = new CalculationUnit(             1, 0,0,1,0,0,0, Unit.PT);
    /**
     * picas
     */
    public static CalculationUnit PC = new CalculationUnit(            12, 0,0,1,0,0,0, Unit.PC);

    /**
     * degrees
     */
    public static CalculationUnit DEG  = new CalculationUnit(          1, 0,0,0,1,0,0, Unit.DEG);
    /**
     * grads/grades/gradians
     */
    public static CalculationUnit GRAD = new CalculationUnit(       9/10, 0,0,0,1,0,0, Unit.GRAD);
    /**
     * radians
     */
    public static CalculationUnit RAD  = new CalculationUnit(180/Math.PI, 0,0,0,1,0,0, Unit.RAD);
    /**
     * turns/revolutions
     */
    public static CalculationUnit TURN = new CalculationUnit(        360, 0,0,0,1,0,0, Unit.TURN);

    /**
     * milliseconds
     */
    public static CalculationUnit MS = new CalculationUnit(   1, 0,0,0,0,1,0, Unit.MS);
    /**
     * seconds
     */
    public static CalculationUnit S  = new CalculationUnit(1000, 0,0,0,0,1,0, Unit.S);

    /**
     * Hertz (1/s)
     */
    public static CalculationUnit Hz  = new CalculationUnit(   1, 0,0,0,0,-1,0, Unit.Hz);
    /**
     * kilohertz
     */
    public static CalculationUnit kHz = new CalculationUnit(1000, 0,0,0,0,-1,0, Unit.kHz);

    /**
     * em
     */
    public static CalculationUnit EM = new CalculationUnit(  1, 0,0,0,0,0,1, Unit.EM);
    /**
     * ex (1/2 em)
     */
    public static CalculationUnit EX = new CalculationUnit(0.5, 0,0,0,0,0,1, Unit.EX);

    /**
     * Mapping from a CSS {@link Unit} to the corresponding CalculationUnit.
     *
     * @see #fromCssUnit(cssUnit)
     */
    private static Map<Unit, CalculationUnit> cssToUnitMap;
    /**
     * Mapping from a CalculationUnit to the corresponding CSS {@link Unit}.
     *
     * @see #toCssUnit(com.silentmatt.dss.expression.CalculationUnit)
     */
    private static Map<CalculationUnit, Unit> unitToCssMap;
    /**
     * Mapping from a CalculationUnit to it's corresponding canonical unit.
     *
     * @see #getCanonicalUnit(com.silentmatt.dss.expression.CalculationUnit)
     */
    private static Map<CalculationUnit, CalculationUnit> canonicalUnit;

    {
        cssToUnitMap = new HashMap<Unit, CalculationUnit>();
        unitToCssMap = new HashMap<CalculationUnit, Unit>();
        canonicalUnit = new HashMap<CalculationUnit, CalculationUnit>();

        cssToUnitMap.put(Unit.None, CalculationUnit.None);

        cssToUnitMap.put(Unit.PX, CalculationUnit.PX);

        cssToUnitMap.put(Unit.Percent, CalculationUnit.Percent);

        cssToUnitMap.put(Unit.IN, CalculationUnit.IN);
        cssToUnitMap.put(Unit.MM, CalculationUnit.MM);
        cssToUnitMap.put(Unit.CM, CalculationUnit.CM);
        cssToUnitMap.put(Unit.PT, CalculationUnit.PT);
        cssToUnitMap.put(Unit.PC, CalculationUnit.PC);

        cssToUnitMap.put(Unit.DEG, CalculationUnit.DEG);
        cssToUnitMap.put(Unit.GRAD, CalculationUnit.GRAD);
        cssToUnitMap.put(Unit.RAD, CalculationUnit.RAD);
        cssToUnitMap.put(Unit.TURN, CalculationUnit.TURN);

        cssToUnitMap.put(Unit.MS, CalculationUnit.MS);
        cssToUnitMap.put(Unit.S, CalculationUnit.S);

        cssToUnitMap.put(Unit.Hz, CalculationUnit.Hz);
        cssToUnitMap.put(Unit.kHz, CalculationUnit.kHz);

        cssToUnitMap.put(Unit.EM, CalculationUnit.EM);
        cssToUnitMap.put(Unit.EX, CalculationUnit.EX);

        canonicalUnit.put(None, None);
        canonicalUnit.put(PX, PX);
        canonicalUnit.put(Percent, Percent);
        canonicalUnit.put(PT, PT);
        canonicalUnit.put(DEG, DEG);
        canonicalUnit.put(MS, MS);
        canonicalUnit.put(S, S);
        canonicalUnit.put(Hz, Hz);
        canonicalUnit.put(EM, EM);

        unitToCssMap.put(None, Unit.None);
        unitToCssMap.put(PX, Unit.PX);
        unitToCssMap.put(Percent, Unit.Percent);
        unitToCssMap.put(PT, Unit.PT);
        unitToCssMap.put(DEG, Unit.DEG);
        unitToCssMap.put(MS, Unit.MS);
        unitToCssMap.put(S, Unit.S);
        unitToCssMap.put(Hz, Unit.Hz);
        unitToCssMap.put(EM, Unit.EM);
    }

    private double scale;
    private int percent;
    private int pxLength;
    private int fontLength;
    private int length;
    private int angle;
    private int time;
    private Unit cssUnit;

    /**
     * Get the CalculationUnit that corresponds to a given {@link Unit}.
     *
     * @param cssUnit The CSS Unit to convert.
     * @return The corresponding Calculation Unit or null if none exists.
     */
    public static CalculationUnit fromCssUnit(Unit cssUnit) {
        if (cssUnit == null) {
            return None;
        }
        return cssToUnitMap.get(cssUnit);
    }

    /**
     * Convert a CalculationUnit to a CSS {@link Unit}.
     *
     * @param unit the CalculationUnit to convert.
     * @return The corresponding CSS {@link Unit}.
     */
    public static Unit toCssUnit(CalculationUnit unit) {
        return unitToCssMap.get(unit);
    }

    /**
     * Get the base CalculationUnit with the same dimension as a given CalculationUnit.
     * The canonical unit for a given dimension is the unit the others are based on.
     * For example, all absolute lengths are converted into points, so
     * {@code CalculationUnit.getCanonicalUnit(CalculationUnit.IN)} would return CalculationUnit.PT.
     *
     * If no canonical unit is defined, getCanonicalUnit returns the original unit.
     *
     * @param unit The CalculationUnit to convert.
     * @return The Canonical CalculationUnit.
     */
    static CalculationUnit getCanonicalUnit(CalculationUnit unit) {
        CalculationUnit res = canonicalUnit.get(unit);
        return res == null ? unit : res;
    }

    /**
     * Construct a new CalculationUnit.
     *
     * @param scale How many of this unit are in the canonical unit.
     * @param pxLength Pixel length
     * @param percent Percentage
     * @param length Absolute length (inches, points, etc.)
     * @param angle Angle
     * @param time Time (negative for frequency)
     * @param fontLength Font-relative dimension em/ex
     * @param cssUnit The corresponding CSS unit, if applicable
     */
    private CalculationUnit(double scale, int pxLength, int percent, int length, int angle, int time, int fontLength, Unit cssUnit) {
        this.scale = scale;
        this.pxLength = pxLength;
        this.percent = percent;
        this.length = length;
        this.angle = angle;
        this.time = time;
        this.fontLength = fontLength;
        this.cssUnit = cssUnit;
    }

    /**
     * Ensure this unit and other can be added or subtracted.
     *
     * To be compatible, two units must have the same dimensions.
     * Note that pixel length, percentage length, font-relative length, and absolute
     * length are separate dimensions, so you cannot, for example, add pixels and ems.
     *
     * @param other The CalculationUnit to check compatibility with
     * @return true iff this and other have the same dimensions.
     */
    public boolean isAddCompatible(CalculationUnit other) {
        return  pxLength == other.pxLength &&
                percent == other.percent &&
                length == other.length &&
                angle == other.angle &&
                time == other.time &&
                fontLength == other.fontLength;
    }

    /**
     * Multiply this unit by another unit.
     * The resulting unit is the pair-wise sum of the dimensions in each unit.
     * The scale of the resulting unit is 1, since values should already be scaled.
     *
     * @param other The unit to multiply this by.
     * @return The product of this and other
     */
    public CalculationUnit multiply(CalculationUnit other) {
        return new CalculationUnit(1,
                pxLength + other.pxLength,
                percent + other.percent,
                length + other.length,
                angle + other.angle,
                time + other.time,
                fontLength + other.fontLength,
                null);
    }

    /**
     * Divide this unit by another unit.
     * The resulting unit is the pair-wise difference between the dimensions in each unit.
     * The scale of the resulting unit is 1, since values should already be scaled.
     *
     * @param other The unit to divide this by.
     * @return The quotient of this and other.
     */
    public CalculationUnit divide(CalculationUnit other) {
        return new CalculationUnit(1,
                pxLength - other.pxLength,
                percent - other.percent,
                length - other.length,
                angle - other.angle,
                time - other.time,
                fontLength - other.fontLength,
                null);
    }

    /**
     * Compare two units using a lexographic ordering.
     *
     * The scale of the unit is not used in comparison, so different units with
     * the same dimension will compare equal.
     *
     * For purposes of ordering units, the dimensions are compared in this order:
     * pixel length, percentage length, absolute length, angle, time, font-relative length.
     *
     * @param other The unit to compare with this.
     * @return A negative integer, zero, or a positive integer corresponding to
     * less than, equal, or greater than.
     */
    public int compareTo(CalculationUnit other) {
        if (pxLength != other.pxLength) {
            return pxLength - other.pxLength;
        }
        if (percent != other.percent) {
            return percent - other.percent;
        }
        if (length != other.length) {
            return length - other.length;
        }
        if (angle != other.angle) {
            return angle - other.angle;
        }
        if (time != other.time) {
            return time - other.time;
        }
        if (fontLength != other.fontLength) {
            return fontLength - other.fontLength;
        }
        return 0;
    }

    /**
     * Get the hash code for this unit.
     * The hash code is determined only by its dimensions.
     *
     * @return The hash code for this unit.
     */
    @Override
    public int hashCode() {
        return fontLength * 100000 + percent * 10000 + angle * 1000 + length * 100 + pxLength * 10 + time;
    }

    /**
     * Check CalculationUnits for equality.
     *
     * @param obj The object to compare to this.
     * @return true iff obj is a CalculationUnit with the same dimensions as this.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CalculationUnit other = (CalculationUnit) obj;
        if (this.pxLength != other.pxLength) {
            return false;
        }
        if (this.percent != other.percent) {
            return false;
        }
        if (this.length != other.length) {
            return false;
        }
        if (this.angle != other.angle) {
            return false;
        }
        if (this.time != other.time) {
            return false;
        }
        if (this.fontLength != other.fontLength) {
            return false;
        }
        return true;
    }

    /**
     * Convert this CalculationUnit to a String.
     *
     * Unit strings are of the form "px^a em^b %^c pt^d deg^e s^f / px^g em^h %^i pt^j deg^k s^l".
     * Any dimensions with a zero exponent are left out, and the exponent is left out if it is one.
     * If the denominator is one (no dimensions are negative), the trailing " /" is removed.
     *
     * Any unit that corresponds to a CSS {@link Unit} will be a valid CSS unit string.
     *
     * @return A string representation of this unit.
     */
    @Override
    public String toString() {
        CalculationUnit cu = getCanonicalUnit(this);
        if (cu.cssUnit != null) { return cu.cssUnit.toString(); }

        StringBuilder sb = new StringBuilder();
        if (pxLength > 1) {
            sb.append(" px^" + pxLength);
        }
        else if (pxLength == 1) {
            sb.append(" px");
        }

        if (fontLength > 1) {
            sb.append(" em^" + fontLength);
        }
        else if (fontLength == 1) {
            sb.append(" em");
        }

        if (percent > 1) {
            sb.append(" %^" + percent);
        }
        else if (percent == 1) {
            sb.append(" %");
        }

        if (length > 1) {
            sb.append(" pt^" + length);
        }
        else if (length == 1) {
            sb.append(" pt");
        }

        if (this.angle > 1) {
            sb.append(" deg^" + angle);
        }
        else if (angle == 1) {
            sb.append(" deg");
        }

        if (time > 1) {
            sb.append(" s^" + time);
        }
        else if (time == 1) {
            sb.append(" s");
        }

        sb.append("/");

        if (pxLength < -1) {
            sb.append("px^" + -pxLength + " ");
        }
        else if (pxLength == -1) {
            sb.append("px ");
        }

        if (fontLength < -1) {
            sb.append("em^" + -fontLength + " ");
        }
        else if (fontLength == -1) {
            sb.append("em ");
        }

        if (percent < -1) {
            sb.append("%^" + -percent + " ");
        }
        else if (percent == -1) {
            sb.append("% ");
        }

        if (length < -1) {
            sb.append("pt^" + -length + " ");
        }
        else if (length == -1) {
            sb.append("pt ");
        }

        if (this.angle < -1) {
            sb.append("deg^" + -angle + " ");
        }
        else if (angle == -1) {
            sb.append("deg ");
        }

        if (time < -1) {
            sb.append("s^" + -time + " ");
        }
        else if (time == -1) {
            sb.append("s ");
        }

        String res = sb.toString().trim();
        if (res.endsWith("/")) {
            return res.substring(0, res.length() - 1);
        }
        return res;
    }

    /**
     * Get the factor a value must be multiplied by to be converted to the canonical unit.
     *
     * If this is the canonical unit for its dimensions, {@code getScale()} will always return 1.
     *
     * @return The CalculationUnit's scale.
     */
    double getScale() {
        return this.scale;
    }
}
