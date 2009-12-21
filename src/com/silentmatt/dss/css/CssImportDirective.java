package com.silentmatt.dss.css;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Matthew Crumley
 */
public class CssImportDirective extends CssExpressionDirective {
    private final CssMedium medium;

    public CssImportDirective(CssTerm url, CssMedium medium) {
        super(new CssExpression());
        getExpression().getTerms().add(url);
        this.medium = medium;
    }

    public CssMedium getMedium() {
        return medium;
    }

    @Override
    public String toString() {
        return "@import " + getExpression() + " " + medium + ";";
    }

    public String getURLString() {
        return getExpression().getTerms().get(0).toString();
    }

    public URL getURL() throws MalformedURLException {
        return new URL(getURLString());
    }
}
