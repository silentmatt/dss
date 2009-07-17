package com.silentmatt.dss;

import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 * @author matt
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
        return getExpression().getTerms().get(0).getValue();
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
