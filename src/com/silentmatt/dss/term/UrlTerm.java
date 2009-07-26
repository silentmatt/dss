package com.silentmatt.dss.term;

/**
 *
 * @author matt
 */
public class UrlTerm extends Term {
    private String value;

    public UrlTerm(String url) {
        value = url;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String Value) {
        this.value = Value;
    }

    @Override
    public String toString() {
        return "url(" + value + ")";
    }
}
