package com.silentmatt.dss;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author Matthew Crumley
 */
public class Color {
    public static final Color AliceBlue = new Color(0xF0, 0xF8, 0xFF, "AliceBlue");
    public static final Color AntiqueWhite = new Color(0xFA, 0xEB, 0xD7, "AntiqueWhite");
    public static final Color Aqua = new Color(0x00, 0xFF, 0xFF, "Aqua");
    public static final Color Aquamarine = new Color(0x7F, 0xFF, 0xD4, "Aquamarine");
    public static final Color Azure = new Color(0xF0, 0xFF, 0xFF, "Azure");
    public static final Color Beige = new Color(0xF5, 0xF5, 0xDC, "Beige");
    public static final Color Bisque = new Color(0xFF, 0xE4, 0xC4, "Bisque");
    public static final Color Black = new Color(0x00, 0x00, 0x00, "Black");
    public static final Color BlanchedAlmond = new Color(0xFF, 0xEB, 0xCD, "BlanchedAlmond");
    public static final Color Blue = new Color(0x00, 0x00, 0xFF, "Blue");
    public static final Color BlueViolet = new Color(0x8A, 0x2B, 0xE2, "BlueViolet");
    public static final Color Brown = new Color(0xA5, 0x2A, 0x2A, "Brown");
    public static final Color BurlyWood = new Color(0xDE, 0xB8, 0x87, "BurlyWood");
    public static final Color CadetBlue = new Color(0x5F, 0x9E, 0xA0, "CadetBlue");
    public static final Color Chartreuse = new Color(0x7F, 0xFF, 0x00, "Chartreuse");
    public static final Color Chocolate = new Color(0xD2, 0x69, 0x1E, "Chocolate");
    public static final Color Coral = new Color(0xFF, 0x7F, 0x50, "Coral");
    public static final Color CornflowerBlue = new Color(0x64, 0x95, 0xED, "CornflowerBlue");
    public static final Color Cornsilk = new Color(0xFF, 0xF8, 0xDC, "Cornsilk");
    public static final Color Crimson = new Color(0xDC, 0x14, 0x3C, "Crimson");
    public static final Color Cyan = new Color(0x00, 0xFF, 0xFF, "Cyan");
    public static final Color DarkBlue = new Color(0x00, 0x00, 0x8B, "DarkBlue");
    public static final Color DarkCyan = new Color(0x00, 0x8B, 0x8B, "DarkCyan");
    public static final Color DarkGoldenRod = new Color(0xB8, 0x86, 0x0B, "DarkGoldenRod");
    public static final Color DarkGray = new Color(0xA9, 0xA9, 0xA9, "DarkGray");
    public static final Color DarkGreen = new Color(0x00, 0x64, 0x00, "DarkGreen");
    public static final Color DarkKhaki = new Color(0xBD, 0xB7, 0x6B, "DarkKhaki");
    public static final Color DarkMagenta = new Color(0x8B, 0x00, 0x8B, "DarkMagenta");
    public static final Color DarkOliveGreen = new Color(0x55, 0x6B, 0x2F, "DarkOliveGreen");
    public static final Color Darkorange = new Color(0xFF, 0x8C, 0x00, "Darkorange");
    public static final Color DarkOrchid = new Color(0x99, 0x32, 0xCC, "DarkOrchid");
    public static final Color DarkRed = new Color(0x8B, 0x00, 0x00, "DarkRed");
    public static final Color DarkSalmon = new Color(0xE9, 0x96, 0x7A, "DarkSalmon");
    public static final Color DarkSeaGreen = new Color(0x8F, 0xBC, 0x8F, "DarkSeaGreen");
    public static final Color DarkSlateBlue = new Color(0x48, 0x3D, 0x8B, "DarkSlateBlue");
    public static final Color DarkSlateGray = new Color(0x2F, 0x4F, 0x4F, "DarkSlateGray");
    public static final Color DarkTurquoise = new Color(0x00, 0xCE, 0xD1, "DarkTurquoise");
    public static final Color DarkViolet = new Color(0x94, 0x00, 0xD3, "DarkViolet");
    public static final Color DeepPink = new Color(0xFF, 0x14, 0x93, "DeepPink");
    public static final Color DeepSkyBlue = new Color(0x00, 0xBF, 0xFF, "DeepSkyBlue");
    public static final Color DimGray = new Color(0x69, 0x69, 0x69, "DimGray");
    public static final Color DodgerBlue = new Color(0x1E, 0x90, 0xFF, "DodgerBlue");
    public static final Color FireBrick = new Color(0xB2, 0x22, 0x22, "FireBrick");
    public static final Color FloralWhite = new Color(0xFF, 0xFA, 0xF0, "FloralWhite");
    public static final Color ForestGreen = new Color(0x22, 0x8B, 0x22, "ForestGreen");
    public static final Color Fuchsia = new Color(0xFF, 0x00, 0xFF, "Fuchsia");
    public static final Color Gainsboro = new Color(0xDC, 0xDC, 0xDC, "Gainsboro");
    public static final Color GhostWhite = new Color(0xF8, 0xF8, 0xFF, "GhostWhite");
    public static final Color Gold = new Color(0xFF, 0xD7, 0x00, "Gold");
    public static final Color GoldenRod = new Color(0xDA, 0xA5, 0x20, "GoldenRod");
    public static final Color Gray = new Color(0x80, 0x80, 0x80, "Gray");
    public static final Color Green = new Color(0x00, 0x80, 0x00, "Green");
    public static final Color GreenYellow = new Color(0xAD, 0xFF, 0x2F, "GreenYellow");
    public static final Color HoneyDew = new Color(0xF0, 0xFF, 0xF0, "HoneyDew");
    public static final Color HotPink = new Color(0xFF, 0x69, 0xB4, "HotPink");
    public static final Color IndianRed = new Color(0xCD, 0x5C, 0x5C, "IndianRed");
    public static final Color Indigo = new Color(0x4B, 0x00, 0x82, "Indigo");
    public static final Color Ivory = new Color(0xFF, 0xFF, 0xF0, "Ivory");
    public static final Color Khaki = new Color(0xF0, 0xE6, 0x8C, "Khaki");
    public static final Color Lavender = new Color(0xE6, 0xE6, 0xFA, "Lavender");
    public static final Color LavenderBlush = new Color(0xFF, 0xF0, 0xF5, "LavenderBlush");
    public static final Color LawnGreen = new Color(0x7C, 0xFC, 0x00, "LawnGreen");
    public static final Color LemonChiffon = new Color(0xFF, 0xFA, 0xCD, "LemonChiffon");
    public static final Color LightBlue = new Color(0xAD, 0xD8, 0xE6, "LightBlue");
    public static final Color LightCoral = new Color(0xF0, 0x80, 0x80, "LightCoral");
    public static final Color LightCyan = new Color(0xE0, 0xFF, 0xFF, "LightCyan");
    public static final Color LightGoldenRodYellow = new Color(0xFA, 0xFA, 0xD2, "LightGoldenRodYellow");
    public static final Color LightGrey = new Color(0xD3, 0xD3, 0xD3, "LightGrey");
    public static final Color LightGreen = new Color(0x90, 0xEE, 0x90, "LightGreen");
    public static final Color LightPink = new Color(0xFF, 0xB6, 0xC1, "LightPink");
    public static final Color LightSalmon = new Color(0xFF, 0xA0, 0x7A, "LightSalmon");
    public static final Color LightSeaGreen = new Color(0x20, 0xB2, 0xAA, "LightSeaGreen");
    public static final Color LightSkyBlue = new Color(0x87, 0xCE, 0xFA, "LightSkyBlue");
    public static final Color LightSlateGray = new Color(0x77, 0x88, 0x99, "LightSlateGray");
    public static final Color LightSteelBlue = new Color(0xB0, 0xC4, 0xDE, "LightSteelBlue");
    public static final Color LightYellow = new Color(0xFF, 0xFF, 0xE0, "LightYellow");
    public static final Color Lime = new Color(0x00, 0xFF, 0x00, "Lime");
    public static final Color LimeGreen = new Color(0x32, 0xCD, 0x32, "LimeGreen");
    public static final Color Linen = new Color(0xFA, 0xF0, 0xE6, "Linen");
    public static final Color Magenta = new Color(0xFF, 0x00, 0xFF, "Magenta");
    public static final Color Maroon = new Color(0x80, 0x00, 0x00, "Maroon");
    public static final Color MediumAquaMarine = new Color(0x66, 0xCD, 0xAA, "MediumAquaMarine");
    public static final Color MediumBlue = new Color(0x00, 0x00, 0xCD, "MediumBlue");
    public static final Color MediumOrchid = new Color(0xBA, 0x55, 0xD3, "MediumOrchid");
    public static final Color MediumPurple = new Color(0x93, 0x70, 0xD8, "MediumPurple");
    public static final Color MediumSeaGreen = new Color(0x3C, 0xB3, 0x71, "MediumSeaGreen");
    public static final Color MediumSlateBlue = new Color(0x7B, 0x68, 0xEE, "MediumSlateBlue");
    public static final Color MediumSpringGreen = new Color(0x00, 0xFA, 0x9A, "MediumSpringGreen");
    public static final Color MediumTurquoise = new Color(0x48, 0xD1, 0xCC, "MediumTurquoise");
    public static final Color MediumVioletRed = new Color(0xC7, 0x15, 0x85, "MediumVioletRed");
    public static final Color MidnightBlue = new Color(0x19, 0x19, 0x70, "MidnightBlue");
    public static final Color MintCream = new Color(0xF5, 0xFF, 0xFA, "MintCream");
    public static final Color MistyRose = new Color(0xFF, 0xE4, 0xE1, "MistyRose");
    public static final Color Moccasin = new Color(0xFF, 0xE4, 0xB5, "Moccasin");
    public static final Color NavajoWhite = new Color(0xFF, 0xDE, 0xAD, "NavajoWhite");
    public static final Color Navy = new Color(0x00, 0x00, 0x80, "Navy");
    public static final Color OldLace = new Color(0xFD, 0xF5, 0xE6, "OldLace");
    public static final Color Olive = new Color(0x80, 0x80, 0x00, "Olive");
    public static final Color OliveDrab = new Color(0x6B, 0x8E, 0x23, "OliveDrab");
    public static final Color Orange = new Color(0xFF, 0xA5, 0x00, "Orange");
    public static final Color OrangeRed = new Color(0xFF, 0x45, 0x00, "OrangeRed");
    public static final Color Orchid = new Color(0xDA, 0x70, 0xD6, "Orchid");
    public static final Color PaleGoldenRod = new Color(0xEE, 0xE8, 0xAA, "PaleGoldenRod");
    public static final Color PaleGreen = new Color(0x98, 0xFB, 0x98, "PaleGreen");
    public static final Color PaleTurquoise = new Color(0xAF, 0xEE, 0xEE, "PaleTurquoise");
    public static final Color PaleVioletRed = new Color(0xD8, 0x70, 0x93, "PaleVioletRed");
    public static final Color PapayaWhip = new Color(0xFF, 0xEF, 0xD5, "PapayaWhip");
    public static final Color PeachPuff = new Color(0xFF, 0xDA, 0xB9, "PeachPuff");
    public static final Color Peru = new Color(0xCD, 0x85, 0x3F, "Peru");
    public static final Color Pink = new Color(0xFF, 0xC0, 0xCB, "Pink");
    public static final Color Plum = new Color(0xDD, 0xA0, 0xDD, "Plum");
    public static final Color PowderBlue = new Color(0xB0, 0xE0, 0xE6, "PowderBlue");
    public static final Color Purple = new Color(0x80, 0x00, 0x80, "Purple");
    public static final Color Red = new Color(0xFF, 0x00, 0x00, "Red");
    public static final Color RosyBrown = new Color(0xBC, 0x8F, 0x8F, "RosyBrown");
    public static final Color RoyalBlue = new Color(0x41, 0x69, 0xE1, "RoyalBlue");
    public static final Color SaddleBrown = new Color(0x8B, 0x45, 0x13, "SaddleBrown");
    public static final Color Salmon = new Color(0xFA, 0x80, 0x72, "Salmon");
    public static final Color SandyBrown = new Color(0xF4, 0xA4, 0x60, "SandyBrown");
    public static final Color SeaGreen = new Color(0x2E, 0x8B, 0x57, "SeaGreen");
    public static final Color SeaShell = new Color(0xFF, 0xF5, 0xEE, "SeaShell");
    public static final Color Sienna = new Color(0xA0, 0x52, 0x2D, "Sienna");
    public static final Color Silver = new Color(0xC0, 0xC0, 0xC0, "Silver");
    public static final Color SkyBlue = new Color(0x87, 0xCE, 0xEB, "SkyBlue");
    public static final Color SlateBlue = new Color(0x6A, 0x5A, 0xCD, "SlateBlue");
    public static final Color SlateGray = new Color(0x70, 0x80, 0x90, "SlateGray");
    public static final Color Snow = new Color(0xFF, 0xFA, 0xFA, "Snow");
    public static final Color SpringGreen = new Color(0x00, 0xFF, 0x7F, "SpringGreen");
    public static final Color SteelBlue = new Color(0x46, 0x82, 0xB4, "SteelBlue");
    public static final Color Tan = new Color(0xD2, 0xB4, 0x8C, "Tan");
    public static final Color Teal = new Color(0x00, 0x80, 0x80, "Teal");
    public static final Color Thistle = new Color(0xD8, 0xBF, 0xD8, "Thistle");
    public static final Color Tomato = new Color(0xFF, 0x63, 0x47, "Tomato");
    public static final Color Turquoise = new Color(0x40, 0xE0, 0xD0, "Turquoise");
    public static final Color Violet = new Color(0xEE, 0x82, 0xEE, "Violet");
    public static final Color Wheat = new Color(0xF5, 0xDE, 0xB3, "Wheat");
    public static final Color White = new Color(0xFF, 0xFF, 0xFF, "White");
    public static final Color WhiteSmoke = new Color(0xF5, 0xF5, 0xF5, "WhiteSmoke");
    public static final Color Yellow = new Color(0xFF, 0xFF, 0x00, "Yellow");
    public static final Color YellowGreen = new Color(0x9A, 0xCD, 0x32, "YellowGreen");

    private static Map<String, Color> namedColors = new HashMap<String, Color>();
    static {
        for (Field field : Color.class.getFields()) {
            if (Color.class.isAssignableFrom(field.getType())) {
                try {
                    namedColors.put(field.getName().toLowerCase(Locale.ENGLISH), (Color) field.get(null));
                } catch (IllegalArgumentException ex) {
                } catch (IllegalAccessException ex) {
                }
            }
        }
    }

    private final int red, green, blue, alpha;
    private final String name;

    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = 255;
        this.name = null;
    }

    public Color(int red, int green, int blue, String name) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = 255;
        this.name = name.toLowerCase();
    }

    public Color(int red, int green, int blue, int alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.name = null;
    }

    public String toHexString() {
        if ((red & 0x0F) == ((red >> 4) & 0x0F) &&
            (green & 0x0F) == ((green >> 4) & 0x0F) &&
            (blue & 0x0F) == ((blue >> 4) & 0x0F)) {
            return String.format("#%X%X%X", red & 0x0F, green & 0x0F, blue & 0x0F);
        }
        return String.format("#%02X%02X%02X", red, green, blue);
    }

    public String toRGBAString() {
        return "rgba(" + red + ", " + green + ", " + blue + ", " + (alpha / 255.0) + ")";
    }

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

    @Override
    public String toString() {
        return alpha == 255 ? toHexString() : toRGBAString();
    }

    public boolean equals(Color other) {
        return this.red == other.red && this.green == other.green && this.blue == other.blue && this.alpha == other.alpha;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        return equals((Color) obj);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + this.red;
        hash = 67 * hash + this.green;
        hash = 67 * hash + this.blue;
        hash = 67 * hash + this.alpha;
        return hash;
    }

    /**
     * Parse a Color using CSS syntax.
     *
     * @param colorString The String to parse.
     *
     * @return The Color represented by <code>colorString</code>.
     *
     * @throws IllegalArgumentException The String cannot be parsed into a valid Color.
     */
    public static Color parse(String colorString) {
        Color color = namedColors.get(colorString.toLowerCase(Locale.ENGLISH));
        if (color != null) {
            return color;
        }
        throw new IllegalArgumentException(colorString);
    }
}
