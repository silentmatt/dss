package com.silentmatt.dss;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author matt
 */
public class ImportDirective extends ExpressionDirective {
    private Medium medium;

    public ImportDirective(Expression url, Medium medium) {
        super(url);
        setMedium(medium);
    }

    public String getName() {
        return "@import";
    }

    public Medium getMedium() {
        return medium;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    public DirectiveType getType() {
        return DirectiveType.Import;
    }

    @Override
    public String toString() {
        return getName() + " " + getExpression() + " " + medium + ";";
    }

    @Override
    public String toCompactString() {
        return getName() + " " + getExpression().toCompactString() + medium + ";";
    }

    public String getURLString() {
        return getExpression().getTerms().get(0).getValue();
    }

    public URL getURL() throws MalformedURLException {
        return new URL(getURLString());
    }

}
