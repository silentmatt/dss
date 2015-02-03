package com.silentmatt.dss.directive;

import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.css.CssImportDirective;
import com.silentmatt.dss.css.CssMediaQuery;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.css.CssTerm;
import com.silentmatt.dss.evaluator.EvaluationState;
import com.silentmatt.dss.media.MediaQuery;
import com.silentmatt.dss.rule.Rule;
import com.silentmatt.dss.term.UrlTerm;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public final class ImportDirective extends ExpressionDirective {
    private final MediaQuery medium;

    public ImportDirective(UrlTerm url, MediaQuery medium) {
        super(url.toExpression());
        this.medium = medium;
    }

    public MediaQuery getMedium() {
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
    public CssRule evaluate(EvaluationState state, List<Rule> container) {
        return new CssImportDirective(new CssTerm(getExpression().toString()), new CssMediaQuery(medium.toString()));
    }

}
