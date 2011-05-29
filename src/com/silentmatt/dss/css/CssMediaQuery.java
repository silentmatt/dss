package com.silentmatt.dss.css;

/**
 * Represents a CSS3 Media Query, for example: "all and (min-width:500px)"
 * @author Matthew Crumley
 */
public class CssMediaQuery {
    private String query;

    public CssMediaQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return query;
    }
}
