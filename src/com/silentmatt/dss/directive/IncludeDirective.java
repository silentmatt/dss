package com.silentmatt.dss.directive;

import com.silentmatt.dss.*;
import com.silentmatt.dss.directive.ExpressionDirective;
import com.silentmatt.dss.directive.DirectiveType;
import com.silentmatt.dss.term.UrlTerm;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author Matthew Crumley
 */
public class IncludeDirective extends ExpressionDirective {
    private CSSDocument included;

    public IncludeDirective(Expression url) {
        super(url);
    }

    public String getName() {
        return "@include";
    }

    public DirectiveType getType() {
        return DirectiveType.Include;
    }

    public String getURLString() {
        // TODO: Remove need for cast
        return ((UrlTerm) getExpression().getTerms().get(0)).getValue();
    }

    public URL getURL() throws MalformedURLException {
        return new URL(getURLString());
    }

    public CSSDocument getIncludedDocument() {
        return included;
    }

    public void setIncludedDocument(CSSDocument doc) {
        included = doc;
    }
}
