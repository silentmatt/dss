package com.silentmatt.dss.directive;

import com.silentmatt.dss.DSSDocument;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Expression;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.RuleSet;
import com.silentmatt.dss.css.CssLiteralText;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.css.CssRuleList;
import com.silentmatt.dss.term.UrlTerm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class IncludeDirective extends ExpressionDirective {
    private DSSDocument included;
    private final boolean literal;

    public IncludeDirective(UrlTerm url, boolean literal) {
        super(new Expression());
        getExpression().getTerms().add(url);
        this.literal = literal;
    }

    public String getURLString() {
        // TODO: Remove need for cast
        return ((UrlTerm) getExpression().getTerms().get(0)).getValue();
    }

    public URL getURL() throws MalformedURLException {
        return new URL(getURLString());
    }

    public DSSDocument getIncludedDocument() {
        return included;
    }

    public boolean isRaw() {
        return literal;
    }

    @Override
    public String toString() {
        return "@include " + (literal ? "literal " : "") + getExpression() + ";";
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) throws IOException {
        CssRule result = null;
        URL url = new URL(state.getBaseURL(), this.getURLString());

        if (literal) {
            return evaluateLiteral(state, url);
        }

        DSSDocument includedDocument = DSSDocument.parse(url.toString(), state.getErrors());
        if (includedDocument != null) {
            state.pushBaseURL(url, Rule.getRuleSets(includedDocument.getRules()));
            try {
                state.pushScope(new ArrayList<RuleSet>()); // Why do this if pushBaseURL already did?
                try {
                    this.included = includedDocument;
                    if (state.getIncludeCallback() != null) {
                        state.getIncludeCallback().call(url);
                    }
                    return new CssRuleList(Rule.evaluateRules(state, includedDocument.getRules()));
                }
                finally {
                    state.popScope();
                }
            }
            finally {
                state.popBaseURL();
            }
        }
        return result;
    }

    private CssRule evaluateLiteral(EvaluationState state, URL url) throws IOException {
        CssRule result = new CssLiteralText(convertStreamToString(url.openStream()));
        if (result != null && state.getIncludeCallback() != null) {
            state.getIncludeCallback().call(url);
        }
        return result;
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();

        try {
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            // Remove the last newline we added - it gets added back in the output
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
        } finally {
            is.close();
        }

        return sb.toString();
    }
}
