package com.silentmatt.dss;

import com.silentmatt.dss.directive.Directive;
import com.silentmatt.dss.parser.ErrorReporter;
import com.silentmatt.dss.parser.Parser;
import com.silentmatt.dss.parser.Scanner;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class CSSDocument {
    private List<RuleSet> ruleSet = new ArrayList<RuleSet>();
    private String charset;
    private List<Directive> directives = new ArrayList<Directive>();
    private List<Rule> allRules = new ArrayList<Rule>();

    public static CSSDocument parse(String url, ErrorReporter errors) throws IOException {
        return parse(new URL(url), errors);
    }

    public static CSSDocument parse(URL url, ErrorReporter errors) throws IOException {
        return parse(url.openStream(), errors);
    }

    public static CSSDocument parse(InputStream input, ErrorReporter errors) throws IOException {
        Scanner scanner = new Scanner(input);
        return parse(scanner, errors);
    }

    public static CSSDocument parse(Scanner scanner, ErrorReporter errors) {
        Parser p = new Parser(scanner);
        p.errors = errors;
        p.Parse();
        if (p.errors.getErrorCount() > 0) {
            return null;
        }
        else {
            return p.CSSDoc;
        }
    }

    public List<RuleSet> getRuleSets() {
        return Collections.unmodifiableList(ruleSet);
    }

    public void setRuleSets(List<RuleSet> ruleSet) {
        this.ruleSet = ruleSet;
    }

    public void addRuleSet(RuleSet set) {
        allRules.add(set);
        ruleSet.add(set);
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public List<Directive> getDirectives() {
        return Collections.unmodifiableList(directives);
    }

    public void addDirective(Directive directive) {
        allRules.add(directive);
        directives.add(directive);
    }

    public List<Rule> getRules() {
        return allRules;
    }

    public void setDirectives(List<Directive> directives) {
        this.directives = directives;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        for (Rule r : allRules) {
            txt.append(r).append("\n");
        }
        return txt.toString();
    }

    public String toCssString() {
        StringBuilder txt = new StringBuilder();
        for (Rule r : allRules) {
            String ruleString = r.toCssString(0);
            if (ruleString.length() > 0) {
                txt.append(ruleString).append("\n");
            }
        }
        return txt.toString();
    }
}
