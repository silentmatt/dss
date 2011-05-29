package com.silentmatt.dss;

/**
 * Represents a CSS3 Media Query, for example: "all and (min-width:500px)"
 * @author Matthew Crumley
 */
public class MediaQuery {
    private String query;

    public MediaQuery(String query) {
        this.query = query;
    }

    @Override
    public String toString() {
        return query;
    }
}
