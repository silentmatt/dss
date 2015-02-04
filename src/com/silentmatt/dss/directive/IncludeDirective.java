package com.silentmatt.dss.directive;

import com.google.common.collect.ImmutableList;
import com.silentmatt.dss.DSSDocument;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.css.CssLiteralText;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.css.CssRuleList;
import com.silentmatt.dss.declaration.Declaration;
import com.silentmatt.dss.declaration.DeclarationList;
import com.silentmatt.dss.declaration.Expression;
import com.silentmatt.dss.evaluator.EvaluationState;
import com.silentmatt.dss.rule.DeclarationBlock;
import com.silentmatt.dss.rule.Rule;
import com.silentmatt.dss.rule.RuleSet;
import com.silentmatt.dss.term.UrlTerm;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Matthew Crumley
 */
@Immutable
public class IncludeDirective extends ExpressionDirective {
    private final boolean literal;
    private final DeclarationList parameters;

    public IncludeDirective(UrlTerm url, boolean literal, ImmutableList<Declaration> parameters) {
        super(url.toExpression());
        this.literal = literal;
        this.parameters = new DeclarationList(parameters);
    }

    public String getURLString() {
        // TODO: Remove need for cast
        return ((UrlTerm) getExpression().getTerms().get(0)).getValue();
    }

    public URL getURL() throws MalformedURLException {
        return new URL(getURLString());
    }

    public boolean isRaw() {
        return literal;
    }

    @Override
    public String toString(int nesting) {
        String start = Rule.getIndent(nesting);
        StringBuilder txt = new StringBuilder(start);

        txt.append("@include ");
        if (literal) {
            txt.append("literal ");
        }
        txt.append(getExpression());

        if (parameters.isEmpty()) {
            txt.append(";");
        }
        else {
            txt.append(" {");
            txt.append(new DeclarationBlock(parameters).innerString(nesting));
            txt.append("\n").append(start).append("}");
        }

        return txt.toString();
    }

    @Override
    public String toString() {
        return toString(0);
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) throws IOException {
        CssRule result = null;
        URL url = new URL(state.getBaseURL(), this.getURLString());

        if (literal) {
            return evaluateLiteral(state, url);
        }

        DSSDocument includedDocument = DSSDocument.parse(state.getResourceLocator().openResource(url), state.getErrors());
        if (includedDocument != null) {
            state.pushBaseURL(url, Rule.getRuleSets(includedDocument.getRules()));
            try {
                state.pushScope(new ArrayList<RuleSet>()); // Why do this if pushBaseURL already did?
                try {
                    state.pushParameters(setArguments(state, parameters));
                    try {
                        if (state.getIncludeCallback() != null) {
                            state.getIncludeCallback().call(url);
                        }
                        return new CssRuleList(Rule.evaluateRules(state, includedDocument.getRules()));
                    }
                    finally {
                        state.popParameters();
                    }
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
        CssRule result = new CssLiteralText(convertStreamToString(state.getResourceLocator().openResource(url)));
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

    private static Map<String, Expression> setArguments(EvaluationState state, DeclarationList args) {
        Map<String, Expression> parameters = new HashMap<>();
        for (Declaration arg : args) {
            parameters.put(arg.getName(), arg.getExpression());
        }
        return parameters;
    }
}
