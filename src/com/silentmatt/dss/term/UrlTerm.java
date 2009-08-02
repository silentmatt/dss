package com.silentmatt.dss.term;

/**
 *
 * @author matt
 */
public class UrlTerm extends Term {
    private final String value;

    public UrlTerm(String url) {
        super();
        value = url;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "url(" + value + ")";
    }
}
