package com.silentmatt.dss.expression;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author matt
 */
public class CalculationUnit implements Comparable<CalculationUnit> {
    public static CalculationUnit None = new CalculationUnit(1, 0,0,0,0, com.silentmatt.dss.Unit.None);

    public static CalculationUnit PX  = new CalculationUnit( 1, 1,0,0,0, com.silentmatt.dss.Unit.PX);

    public static CalculationUnit IN = new CalculationUnit(            72, 0,1,0,0, com.silentmatt.dss.Unit.IN);
    public static CalculationUnit MM = new CalculationUnit(.0393700787*72, 0,1,0,0, com.silentmatt.dss.Unit.MM);
    public static CalculationUnit CM = new CalculationUnit(0.393700787*72, 0,1,0,0, com.silentmatt.dss.Unit.CM);
    public static CalculationUnit PT = new CalculationUnit(             1, 0,1,0,0, com.silentmatt.dss.Unit.PT);
    public static CalculationUnit PC = new CalculationUnit(            12, 0,1,0,0, com.silentmatt.dss.Unit.PC);

    public static CalculationUnit DEG  = new CalculationUnit(          1, 0,0,1,0, com.silentmatt.dss.Unit.DEG);
    public static CalculationUnit GRAD = new CalculationUnit(       9/10, 0,0,1,0, com.silentmatt.dss.Unit.GRAD);
    public static CalculationUnit RAD  = new CalculationUnit(180/Math.PI, 0,0,1,0, com.silentmatt.dss.Unit.RAD);
    public static CalculationUnit TURN = new CalculationUnit(        360, 0,0,1,0, com.silentmatt.dss.Unit.TURN);

    public static CalculationUnit MS = new CalculationUnit(   1, 0,0,0,1, com.silentmatt.dss.Unit.MS);
    public static CalculationUnit S  = new CalculationUnit(1000, 0,0,0,1, com.silentmatt.dss.Unit.S);

    public static CalculationUnit Hz  = new CalculationUnit(   1, 0,0,0,-1, com.silentmatt.dss.Unit.Hz);
    public static CalculationUnit kHz = new CalculationUnit(1000, 0,0,0,-1, com.silentmatt.dss.Unit.kHz);

    private static Map<com.silentmatt.dss.Unit, CalculationUnit> cssToUnitMap;
    private static Map<CalculationUnit, com.silentmatt.dss.Unit> unitToCssMap;
    private static Map<CalculationUnit, CalculationUnit> canonicalUnit;

    {
        cssToUnitMap = new HashMap<com.silentmatt.dss.Unit, CalculationUnit>();
        unitToCssMap = new HashMap<CalculationUnit, com.silentmatt.dss.Unit>();
        canonicalUnit = new HashMap<CalculationUnit, CalculationUnit>();

        cssToUnitMap.put(com.silentmatt.dss.Unit.None, CalculationUnit.None);
        cssToUnitMap.put(com.silentmatt.dss.Unit.PX, CalculationUnit.PX);

        cssToUnitMap.put(com.silentmatt.dss.Unit.IN, CalculationUnit.IN);
        cssToUnitMap.put(com.silentmatt.dss.Unit.MM, CalculationUnit.MM);
        cssToUnitMap.put(com.silentmatt.dss.Unit.CM, CalculationUnit.CM);
        cssToUnitMap.put(com.silentmatt.dss.Unit.PT, CalculationUnit.PT);
        cssToUnitMap.put(com.silentmatt.dss.Unit.PC, CalculationUnit.PC);

        cssToUnitMap.put(com.silentmatt.dss.Unit.DEG, CalculationUnit.DEG);
        cssToUnitMap.put(com.silentmatt.dss.Unit.GRAD, CalculationUnit.GRAD);
        cssToUnitMap.put(com.silentmatt.dss.Unit.RAD, CalculationUnit.RAD);
        cssToUnitMap.put(com.silentmatt.dss.Unit.TURN, CalculationUnit.TURN);

        cssToUnitMap.put(com.silentmatt.dss.Unit.MS, CalculationUnit.MS);
        cssToUnitMap.put(com.silentmatt.dss.Unit.S, CalculationUnit.S);

        cssToUnitMap.put(com.silentmatt.dss.Unit.Hz, CalculationUnit.Hz);
        cssToUnitMap.put(com.silentmatt.dss.Unit.kHz, CalculationUnit.kHz);

        canonicalUnit.put(None, None);
        canonicalUnit.put(PX, PX);
        canonicalUnit.put(PT, PT);
        canonicalUnit.put(DEG, DEG);
        canonicalUnit.put(MS, MS);
        canonicalUnit.put(S, S);
        canonicalUnit.put(Hz, Hz);

        unitToCssMap.put(None, com.silentmatt.dss.Unit.None);
        unitToCssMap.put(PX, com.silentmatt.dss.Unit.PX);
        unitToCssMap.put(PT, com.silentmatt.dss.Unit.PT);
        unitToCssMap.put(DEG, com.silentmatt.dss.Unit.DEG);
        unitToCssMap.put(MS, com.silentmatt.dss.Unit.MS);
        unitToCssMap.put(S, com.silentmatt.dss.Unit.S);
        unitToCssMap.put(Hz, com.silentmatt.dss.Unit.Hz);
    }

    private double scale;
    private int pxLength;
    private int length;
    private int angle;
    private int time;
    private com.silentmatt.dss.Unit cssUnit;

    public static CalculationUnit fromCssUnit(com.silentmatt.dss.Unit cssUnit) {
        if (cssUnit == null) {
            return None;
        }
        return cssToUnitMap.get(cssUnit);
    }

    public static com.silentmatt.dss.Unit toCssUnit(CalculationUnit unit) {
        return unitToCssMap.get(unit);
    }

    public static CalculationUnit getCanonicalUnit(CalculationUnit unit) {
        CalculationUnit res = canonicalUnit.get(unit);
        return res == null ? unit : res;
    }

    private CalculationUnit(double scale, int pxLength, int length, int angle, int time, com.silentmatt.dss.Unit cssUnit) {
        this.scale = scale;
        this.pxLength = pxLength;
        this.length = length;
        this.angle = angle;
        this.time = time;
        this.cssUnit = cssUnit;
    }

    public boolean isAddCompatible(CalculationUnit other) {
        return pxLength == other.pxLength &&
                length == other.length &&
                angle == other.angle &&
                time == other.time;
    }

    public CalculationUnit multiply(CalculationUnit other) {
        return new CalculationUnit(1,
                pxLength + other.pxLength,
                length + other.length,
                angle + other.angle,
                time + other.time,
                null);
    }

    public CalculationUnit divide(CalculationUnit other) {
        return new CalculationUnit(1,
                pxLength - other.pxLength,
                length - other.length,
                angle - other.angle,
                time - other.time,
                null);
    }

    public int compareTo(CalculationUnit other) {
        if (pxLength != other.pxLength) {
            return pxLength - other.pxLength;
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
        return 0;
    }

    @Override
    public int hashCode() {
        return angle * 1000 + length * 100 + pxLength * 10 + time;
    }

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
        if (this.length != other.length) {
            return false;
        }
        if (this.angle != other.angle) {
            return false;
        }
        if (this.time != other.time) {
            return false;
        }
        return true;
    }

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

    double getScale() {
        return this.scale;
    }
}
