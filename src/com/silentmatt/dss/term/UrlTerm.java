package com.silentmatt.dss.term;

import com.silentmatt.dss.Immutable;

/**
 * A URL term.
 *
 * @author Matthew Crumley
 */
@Immutable
public final class UrlTerm extends Term {
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
        super(null);
        value = url;
    }

    /**
     * Constructs a UrlTerm from a url string and a separator.
     *
     * @param sep The separator
     * @param url The URL
     */
    public UrlTerm(Character sep, String url) {
        super(sep);
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

    @Override
    public UrlTerm withSeparator(Character separator) {
        return new UrlTerm(separator, value);
    }
}
