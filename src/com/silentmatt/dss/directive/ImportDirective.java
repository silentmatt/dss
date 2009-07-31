package com.silentmatt.dss.directive;

import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Medium;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.term.UrlTerm;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

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

    public Medium getMedium() {
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

    @Override
    public void evaluate(EvaluationState state, List<Rule> container) {
        // Do nothing
    }

}
