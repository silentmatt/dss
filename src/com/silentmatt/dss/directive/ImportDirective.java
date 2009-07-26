package com.silentmatt.dss.directive;

import com.silentmatt.dss.*;
import com.silentmatt.dss.directive.ExpressionDirective;
import com.silentmatt.dss.directive.DirectiveType;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Matthew Crumley
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

    public String getURLString() {
        return getExpression().getTerms().get(0).toString();
    }

    public URL getURL() throws MalformedURLException {
        return new URL(getURLString());
    }

}
