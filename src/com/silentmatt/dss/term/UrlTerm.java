package com.silentmatt.dss.term;

/**
 * A URL term.
 *
 * @author matt
 */
public class UrlTerm extends Term {
    /**
     * The URL string.
     */
    private final String value;

    /**
     * Constructs a UrlTerm from a url string.
     *
     * @param url The URL
     */
    public UrlTerm(String url) {
        super();
        value = url;
    }

    /**
     * Gets the URL string.
     *
     * @return The URL string
     */
    public String getValue() {
        return value;
    }

    /**
     * Converts this UrlTerm into a String.
     *
     * @return A string of the form "url(value)"
     */
    @Override
    public String toString() {
        return "url(" + value + ")";
    }
}
