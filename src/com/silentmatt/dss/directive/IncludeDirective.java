package com.silentmatt.dss.directive;

import com.silentmatt.dss.CSSDocument;
import com.silentmatt.dss.DSSEvaluator;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.term.UrlTerm;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class IncludeDirective extends ExpressionDirective {
    private CSSDocument included;

    public IncludeDirective(UrlTerm url) {
        super(new Expression());
        getExpression().getTerms().add(url);
    }

    public IncludeDirective(Expression url) {
        super(url);
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

    @Override
    public String toString() {
        return "@include " + getExpression() + ";";
    }

    @Override
    public void evaluate(DSSEvaluator.EvaluationState state, List<Rule> container) throws MalformedURLException, IOException {
        URL url = new URL(state.getBaseURL(), this.getURLString());
        CSSDocument includedDocument = CSSDocument.parse(url.toString(), state.getErrors());
        if (includedDocument != null) {
            state.pushBaseURL(url);
            try {
                // Evaluate the first rule, since it's in the same index as the include
                if (includedDocument.getRules().size() > 0) {
                    Rule.evaluateRules(state, includedDocument.getRules().subList(0, 1));
                }
                this.setIncludedDocument(includedDocument);
            }
            finally {
                state.popBaseURL();
            }

            int index = container.indexOf(this);
            if (index != -1) {
                container.remove(index);
                for (Rule r : includedDocument.getRules()) {
                    container.add(index, r);
                    index++;
                }
            }
        }
    }
}
