package com.silentmatt.dss.directive;

import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Medium;
import com.silentmatt.dss.term.UrlTerm;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Matthew Crumley
 */
public class ImportDirective extends ExpressionDirective {
    private Medium medium;

    public ImportDirective(UrlTerm url, Medium medium) {
        super(new Expression());
        getExpression().getTerms().add(url);
        this.medium = medium;
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
