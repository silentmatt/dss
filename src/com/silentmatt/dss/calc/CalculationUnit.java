package com.silentmatt.dss.calc;

import com.silentmatt.dss.Immutable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a CSS unit that can be used in calculations.
 * @author Matthew Crumley
 */
@Immutable
public final class CalculationUnit implements Comparable<CalculationUnit> {
    /**
     * a scalar (dimensionless) unit
     */
    public static final CalculationUnit None = new CalculationUnit(1, 0,0,0,0,0,0,0,0,0,0,0,0, Unit.None);

    /**
     * pixels
     */
    public static final CalculationUnit PX = new CalculationUnit( 1, 1,0,0,0,0,0,0,0,0,0,0,0, Unit.PX);

    /**
     * percentage
     */
    public static final CalculationUnit Percent = new CalculationUnit( 1, 0,1,0,0,0,0,0,0,0,0,0,0, Unit.Percent);

    /**
     * inches
     */
    public static final CalculationUnit IN = new CalculationUnit(            72, 0,0,1,0,0,0,0,0,0,0,0,0, Unit.IN);
    /**
     * millimeters
     */
    public static final CalculationUnit MM = new CalculationUnit(.0393700787*72, 0,0,1,0,0,0,0,0,0,0,0,0, Unit.MM);
    /**
     * centimeters
     */
    public static final CalculationUnit CM = new CalculationUnit(0.393700787*72, 0,0,1,0,0,0,0,0,0,0,0,0, Unit.CM);
    /**
     * points (1/72 of an inch)
     */
    public static final CalculationUnit PT = new CalculationUnit(             1, 0,0,1,0,0,0,0,0,0,0,0,0, Unit.PT);
    /**
     * picas
     */
    public static final CalculationUnit PC = new CalculationUnit(            12, 0,0,1,0,0,0,0,0,0,0,0,0, Unit.PC);

    /**
     * degrees
     */
    public static final CalculationUnit DEG  = new CalculationUnit(          1, 0,0,0,1,0,0,0,0,0,0,0,0, Unit.DEG);
    /**
     * grads/grades/gradians
     */
    public static final CalculationUnit GRAD = new CalculationUnit(   9.0/10.0, 0,0,0,1,0,0,0,0,0,0,0,0, Unit.GRAD);
    /**
     * radians
     */
    public static final CalculationUnit RAD  = new CalculationUnit(180/Math.PI, 0,0,0,1,0,0,0,0,0,0,0,0, Unit.RAD);
    /**
     * turns/revolutions
     */
    public static final CalculationUnit TURN = new CalculationUnit(        360, 0,0,0,1,0,0,0,0,0,0,0,0, Unit.TURN);

    /**
     * milliseconds
     */
    public static final CalculationUnit MS = new CalculationUnit(   1, 0,0,0,0,1,0,0,0,0,0,0,0, Unit.MS);
    /**
     * seconds
     */
    public static final CalculationUnit S  = new CalculationUnit(1000, 0,0,0,0,1,0,0,0,0,0,0,0, Unit.S);

    /**
     * Hertz (1/s)
     */
    public static final CalculationUnit Hz  = new CalculationUnit(   1, 0,0,0,0,-1,0,0,0,0,0,0,0, Unit.Hz);
    /**
     * kilohertz
     */
    public static final CalculationUnit kHz = new CalculationUnit(1000, 0,0,0,0,-1,0,0,0,0,0,0,0, Unit.kHz);

    /**
     * em
     */
    public static final CalculationUnit EM = new CalculationUnit(  1, 0,0,0,0,0,1,0,0,0,0,0,0, Unit.EM);
    /**
     * ex (1/2 em)
     */
    public static final CalculationUnit EX = new CalculationUnit(0.5, 0,0,0,0,0,1,0,0,0,0,0,0, Unit.EX);

    /**
     * layout-grid size
     */
    public static final CalculationUnit GD  = new CalculationUnit(1, 0,0,0,0,0,0,1,0,0,0,0,0, Unit.GD);

    /**
     * root element font size (rem)
     */
    public static final CalculationUnit REM = new CalculationUnit(1, 0,0,0,0,0,0,0,1,0,0,0,0, Unit.REM);

    /**
     * viewport width
     */
    public static final CalculationUnit VW  = new CalculationUnit(1, 0,0,0,0,0,0,0,0,1,0,0,0, Unit.VW);

    /**
     * viewport height
     */
    public static final CalculationUnit VH  = new CalculationUnit(1, 0,0,0,0,0,0,0,0,0,1,0,0, Unit.VH);

    /**
     * Minumum of viewport height or width
     */
    public static final CalculationUnit VM  = new CalculationUnit(1, 0,0,0,0,0,0,0,0,0,0,1,0, Unit.VM);

    /**
     * Width of the "0" character
     */
    public static final CalculationUnit CH  = new CalculationUnit(1, 0,0,0,0,0,0,0,0,0,0,0,1, Unit.CH);


    /**
     * Mapping from a CSS {@link Unit} to the corresponding CalculationUnit.
     *
     * @see #fromCssUnit(cssUnit)
     */
    private static final Map<Unit, CalculationUnit> cssToUnitMap;
    /**
     * Mapping from a CalculationUnit to the corresponding CSS {@link Unit}.
     *
     * @see #toCssUnit(com.silentmatt.dss.expression.CalculationUnit)
     */
    private static final Map<CalculationUnit, Unit> unitToCssMap;
    /**
     * Mapping from a CalculationUnit to it's corresponding canonical unit.
     *
     * @see #getCanonicalUnit(com.silentmatt.dss.expression.CalculationUnit)
     */
    private static final Map<CalculationUnit, CalculationUnit> canonicalUnit;

    static {
        cssToUnitMap = new EnumMap<Unit, CalculationUnit>(Unit.class);
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

        cssToUnitMap.put(Unit.GD, CalculationUnit.GD);
        cssToUnitMap.put(Unit.REM, CalculationUnit.REM);
        cssToUnitMap.put(Unit.VW, CalculationUnit.VW);
        cssToUnitMap.put(Unit.VH, CalculationUnit.VH);
        cssToUnitMap.put(Unit.VM, CalculationUnit.VM);
        cssToUnitMap.put(Unit.CH, CalculationUnit.CH);

        canonicalUnit.put(None, None);
        canonicalUnit.put(PX, PX);
        canonicalUnit.put(Percent, Percent);
        canonicalUnit.put(PT, PT);
        canonicalUnit.put(DEG, DEG);
        canonicalUnit.put(MS, MS);
        canonicalUnit.put(S, S);
        canonicalUnit.put(Hz, Hz);
        canonicalUnit.put(EM, EM);
        canonicalUnit.put(GD, GD);
        canonicalUnit.put(REM, REM);
        canonicalUnit.put(VW, VW);
        canonicalUnit.put(VH, VH);
        canonicalUnit.put(VM, VM);
        canonicalUnit.put(CH, CH);

        unitToCssMap.put(None, Unit.None);
        unitToCssMap.put(PX, Unit.PX);
        unitToCssMap.put(Percent, Unit.Percent);
        unitToCssMap.put(PT, Unit.PT);
        unitToCssMap.put(DEG, Unit.DEG);
        unitToCssMap.put(MS, Unit.MS);
        unitToCssMap.put(S, Unit.S);
        unitToCssMap.put(Hz, Unit.Hz);
        unitToCssMap.put(EM, Unit.EM);
        unitToCssMap.put(GD, Unit.GD);
        unitToCssMap.put(REM, Unit.REM);
        unitToCssMap.put(VW, Unit.VW);
        unitToCssMap.put(VH, Unit.VH);
        unitToCssMap.put(VM, Unit.VM);
        unitToCssMap.put(CH, Unit.CH);
    }

    private final double scale;
    private final int percent;
    private final int pxLength;
    private final int fontLength;
    private final int length;
    private final int angle;
    private final int time;
    private final int gdLength;
    private final int remLength;
    private final int vwLength;
    private final int vhLength;
    private final int vmLength;
    private final int chLength;
    private final Unit cssUnit;

    /**
     * Gets the CalculationUnit that corresponds to a given {@link Unit}.
     *
     * @param cssUnit The CSS Unit to convert.
     * @return The corresponding CalculationUnit or null if none exists.
     */
    public static CalculationUnit fromCssUnit(Unit cssUnit) {
        if (cssUnit == null) {
            return None;
        }
        return cssToUnitMap.get(cssUnit);
    }

    /**
     * Converts a CalculationUnit to a CSS {@link Unit}.
     *
     * @param unit the CalculationUnit to convert.
     * @return The corresponding CSS {@link Unit}.
     */
    public static Unit toCssUnit(CalculationUnit unit) {
        return unitToCssMap.get(unit);
    }

    /**
     * Gets the base CalculationUnit with the same dimension as a given CalculationUnit.
     * The canonical unit for a given dimension is the unit the others are based on.
     * For example, all absolute lengths are converted into points, so
     * {@code CalculationUnit.getCanonicalUnit(CalculationUnit.IN)} would return CalculationUnit.PT.
     *
     * If no canonical unit is defined, getCanonicalUnit returns the original unit.
     *
     * @param unit The CalculationUnit to convert.
     * @return The Canonical CalculationUnit.
     */
    public static CalculationUnit getCanonicalUnit(CalculationUnit unit) {
        CalculationUnit res = canonicalUnit.get(unit);
        return res == null ? unit : res;
    }

    /**
     * Constructs a new CalculationUnit.
     *
     * @param scale How many of this unit are in the canonical unit.
     * @param pxLength Pixel length
     * @param percent Percentage
     * @param length Absolute length (inches, points, etc.)
     * @param angle Angle
     * @param time Time (negative for frequency)
     * @param fontLength Font-relative dimension em/ex
     * @param gdLength Grid-relative dimension (gd)
     * @param remLength Root element length
     * @param vwLength Viewport-width-relative dimension
     * @param vhLength Viewport-height-relative dimension
     * @param vmLength Viewport-relative dimension
     * @param chLength "0"-character-relative dimension
     * @param cssUnit The corresponding CSS unit, if applicable
     */
    private CalculationUnit(double scale, int pxLength, int percent, int length, int angle, int time, int fontLength, int gdLength, int remLength, int vwLength, int vhLength, int vmLength, int chLength, Unit cssUnit) {
        this.scale = scale;
        this.pxLength = pxLength;
        this.percent = percent;
        this.length = length;
        this.angle = angle;
        this.time = time;
        this.fontLength = fontLength;
        this.gdLength = gdLength;
        this.remLength = remLength;
        this.vwLength = vwLength;
        this.vhLength = vhLength;
        this.vmLength = vmLength;
        this.chLength = chLength;
        this.cssUnit = cssUnit;
    }

    /**
     * Checks if this unit and <code>other</code> can be added or subtracted.
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
                fontLength == other.fontLength &&
                gdLength == other.gdLength &&
                remLength == other.remLength &&
                vwLength == other.vwLength &&
                vhLength == other.vhLength &&
                vmLength == other.vmLength &&
                chLength == other.chLength;
    }

    /**
     * Multiplies this unit by another unit.
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
                gdLength + other.gdLength,
                remLength + other.remLength,
                vwLength + other.vwLength,
                vhLength + other.vhLength,
                vmLength + other.vmLength,
                chLength + other.chLength,
                null);
    }

    /**
     * Divides this unit by another unit.
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
                gdLength - other.gdLength,
                remLength - other.remLength,
                vwLength - other.vwLength,
                vhLength - other.vhLength,
                vmLength - other.vmLength,
                chLength - other.chLength,
                null);
    }

    /**
     * Compares two units using a lexographic ordering.
     *
     * The scale of the unit is not used in comparison, so different units with
     * the same dimension will compare equal.
     *
     * For purposes of ordering units, the dimensions are compared in this order:
     * pixel length, percentage length, absolute length, angle, time, font-relative length.
     *
     * @param other The unit to compare with <code>this</code>.
     * @return A negative integer, zero, or a positive integer corresponding to
     * less than, equal, or greater than.
     */
    @Override
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
        if (gdLength != other.gdLength) {
            return gdLength - other.gdLength;
        }
        if (remLength != other.remLength) {
            return remLength - other.remLength;
        }
        if (vwLength != other.vwLength) {
            return vwLength - other.vwLength;
        }
        if (vhLength != other.vhLength) {
            return vhLength - other.vhLength;
        }
        if (vmLength != other.vmLength) {
            return vmLength - other.vmLength;
        }
        if (chLength != other.chLength) {
            return chLength - other.chLength;
        }
        return 0;
    }

    /**
     * Gets the hash code for this unit.
     * The hash code is determined only by its dimensions.
     *
     * @return The hash code for this unit.
     */
    @Override
    public int hashCode() {
        return  chLength   * 1000000 +
                vmLength   * 1000000 +
                vhLength   * 1000000 +
                vwLength   * 1000000 +
                remLength  * 1000000 +
                gdLength   * 1000000 +
                fontLength * 100000 +
                percent    * 10000 +
                angle      * 1000 +
                length     * 100 +
                pxLength   * 10 +
                time;
    }

    /**
     * Checks CalculationUnits for equality.
     *
     * @param obj The object to compare to <code>this</code>.
     * @return true iff <code>obj</code> is a <code>CalculationUnit</code> with
     * the same dimensions as <code>this</code>.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CalculationUnit other = (CalculationUnit) obj;
        return this.pxLength == other.pxLength &&
               this.percent == other.percent &&
               this.length == other.length &&
               this.angle == other.angle &&
               this.time == other.time &&
               this.fontLength == other.fontLength &&
               this.gdLength == other.gdLength &&
               this.remLength == other.remLength &&
               this.vwLength == other.vwLength &&
               this.vhLength == other.vhLength &&
               this.vmLength == other.vmLength &&
               this.chLength == other.chLength;
    }

    /**
     * Converts this <code>CalculationUnit</code> to a <code>String</code>.
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
        CalculationUnit canonical = getCanonicalUnit(this);
        if (canonical.cssUnit != null) { return canonical.cssUnit.toString(); }

        StringBuilder sb = new StringBuilder();
        if (pxLength > 1) {
            sb.append(" px^").append(pxLength);
        }
        else if (pxLength == 1) {
            sb.append(" px");
        }

        if (fontLength > 1) {
            sb.append(" em^").append(fontLength);
        }
        else if (fontLength == 1) {
            sb.append(" em");
        }

        if (percent > 1) {
            sb.append(" %^").append(percent);
        }
        else if (percent == 1) {
            sb.append(" %");
        }

        if (length > 1) {
            sb.append(" pt^").append(length);
        }
        else if (length == 1) {
            sb.append(" pt");
        }

        if (this.angle > 1) {
            sb.append(" deg^").append(angle);
        }
        else if (angle == 1) {
            sb.append(" deg");
        }

        if (time > 1) {
            sb.append(" s^").append(time);
        }
        else if (time == 1) {
            sb.append(" s");
        }

        if (gdLength > 1) {
            sb.append(" gd^").append(gdLength);
        }
        else if (gdLength == 1) {
            sb.append(" gd");
        }

        if (remLength > 1) {
            sb.append(" rem^").append(remLength);
        }
        else if (remLength == 1) {
            sb.append(" rem");
        }

        if (vwLength > 1) {
            sb.append(" vw^").append(vwLength);
        }
        else if (vwLength == 1) {
            sb.append(" vw");
        }

        if (vhLength > 1) {
            sb.append(" vh^").append(vhLength);
        }
        else if (vhLength == 1) {
            sb.append(" vh");
        }

        if (vmLength > 1) {
            sb.append(" vm^").append(vmLength);
        }
        else if (vmLength == 1) {
            sb.append(" vm");
        }

        if (chLength > 1) {
            sb.append(" ch^").append(chLength);
        }
        else if (chLength == 1) {
            sb.append(" ch");
        }

        sb.append("/");

        if (pxLength < -1) {
            sb.append("px^").append(-pxLength).append(" ");
        }
        else if (pxLength == -1) {
            sb.append("px ");
        }

        if (fontLength < -1) {
            sb.append("em^").append(-fontLength).append(" ");
        }
        else if (fontLength == -1) {
            sb.append("em ");
        }

        if (percent < -1) {
            sb.append("%^").append(-percent).append(" ");
        }
        else if (percent == -1) {
            sb.append("% ");
        }

        if (length < -1) {
            sb.append("pt^").append(-length).append(" ");
        }
        else if (length == -1) {
            sb.append("pt ");
        }

        if (this.angle < -1) {
            sb.append("deg^").append(-angle).append(" ");
        }
        else if (angle == -1) {
            sb.append("deg ");
        }

        if (time < -1) {
            sb.append("s^").append(-time).append(" ");
        }
        else if (time == -1) {
            sb.append("s ");
        }

        if (gdLength < -1) {
            sb.append("gd^").append(-gdLength).append(" ");
        }
        else if (gdLength == -1) {
            sb.append("gd ");
        }

        if (remLength < -1) {
            sb.append("rem^").append(-remLength).append(" ");
        }
        else if (remLength == -1) {
            sb.append("rem ");
        }

        if (vwLength < -1) {
            sb.append("vw^").append(-vwLength).append(" ");
        }
        else if (vwLength == -1) {
            sb.append("vw ");
        }

        if (vhLength < -1) {
            sb.append("vh^").append(-vhLength).append(" ");
        }
        else if (vhLength == -1) {
            sb.append("vh ");
        }

        if (vmLength < -1) {
            sb.append("vm^").append(-vmLength).append(" ");
        }
        else if (vmLength == -1) {
            sb.append("vm ");
        }

        if (chLength < -1) {
            sb.append("ch^").append(-chLength).append(" ");
        }
        else if (chLength == -1) {
            sb.append("ch ");
        }

        String res = sb.toString().trim();
        if (res.endsWith("/")) {
            res = res.substring(0, res.length() - 1);
        }
        return res;
    }

    /**
     * Gets the factor a value must be multiplied by to be converted to the canonical unit.
     *
     * If this is the canonical unit for its dimensions, {@code getScale()} will always return 1.
     *
     * @return The <code>CalculationUnit</code>'s scale.
     */
    public double getScale() {
        return this.scale;
    }
}
