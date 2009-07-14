package com.silentmatt.dss;

/**
 *
 * @author matt
 */
public enum Unit {
    None,
    Percent,
    EM,
    EX,
    PX,
    GD,
    REM,
    VW,
    VH,
    VM,
    CH,
    MM,
    CM,
    IN,
    PT,
    PC,
    DEG,
    GRAD,
    RAD,
    TURN,
    MS,
    S,
    Hz,
    kHz;

    @Override
    public String toString() {
        if (this == Unit.Percent) {
            return "%";
        } else if (this == Unit.Hz || this == Unit.kHz) {
            return super.toString();
        } else if (this == Unit.None) {
            return "";
        }
        return super.toString().toLowerCase();
    }

    public static Unit parse(String s) {
        for (Unit m : Unit.values()) {
            if (s.equalsIgnoreCase(m.toString())) {
                return m;
            }
        }
        throw new IllegalArgumentException(s);
    }
}
