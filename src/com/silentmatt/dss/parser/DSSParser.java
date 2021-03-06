package com.silentmatt.dss.parser;

import com.google.common.base.Charsets;
import com.silentmatt.dss.DSSDocument;
import com.silentmatt.dss.declaration.Declaration;
import com.silentmatt.dss.declaration.Expression;
import com.silentmatt.dss.directive.CharsetDirective;
import com.silentmatt.dss.directive.ClassDirective;
import com.silentmatt.dss.directive.DefineDirective;
import com.silentmatt.dss.directive.FontFaceDirective;
import com.silentmatt.dss.directive.IfDirective;
import com.silentmatt.dss.directive.ImportDirective;
import com.silentmatt.dss.directive.IncludeDirective;
import com.silentmatt.dss.directive.MediaDirective;
import com.silentmatt.dss.directive.NamespaceDirective;
import com.silentmatt.dss.directive.PageDirective;
import com.silentmatt.dss.error.ErrorReporter;
import com.silentmatt.dss.error.ExceptionErrorReporter;
import com.silentmatt.dss.error.NullErrorReporter;
import com.silentmatt.dss.rule.Rule;
import com.silentmatt.dss.rule.RuleSet;
import com.silentmatt.dss.selector.Selector;
import com.silentmatt.dss.term.Term;
import java.io.ByteArrayInputStream;

/**
 *
 * @author Matthew Crumley
 */
public class DSSParser {
    private final Parser parser;

    public DSSParser(Scanner scanner) {
        parser = new Parser(scanner);
    }

    public void setErrors(ErrorReporter errors) {
        parser.errors = errors;
    }

    public ErrorReporter getErrors() {
        return parser.errors;
    }

    public void parse() {
        parser.Parse();
    }

    public DSSDocument getDocument() {
        return parser.CSSDoc;
    }

    public static String getErrorMessage(int n) {
        return Parser.getErrorMessage(n);
    }

    private static Parser getParser(String text, ErrorReporter errors) {
        Parser parser = new Parser(new Scanner(new ByteArrayInputStream(text.getBytes(Charsets.UTF_8))));
        parser.errors = errors != null ? errors : new ExceptionErrorReporter(new NullErrorReporter());
        parser.la = new Token();
		parser.la.val = "";
		parser.Get();
        return parser;
    }

    public static DSSDocument parseDocument(String text) {
        return parseDocument(text, null);
    }

    public static DSSDocument parseDocument(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        parser.CSS3();
        parser.Expect(0);
        return parser.CSSDoc;
    }

    public static Rule parseDirective(String text) {
        return parseDirective(text, null);
    }

    public static Rule parseDirective(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        Rule result = parser.directive();
        parser.Expect(0);
        return result;
    }

    public static MediaDirective parseMedia(String text) {
        return parseMedia(text, null);
    }

    public static MediaDirective parseMedia(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        MediaDirective result = parser.mediaDirective();
        parser.Expect(0);
        return result;
    }

    public static IfDirective parseIf(String text) {
        return parseIf(text, null);
    }

    public static IfDirective parseIf(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        IfDirective result = parser.ifDirective();
        parser.Expect(0);
        return result;
    }

    public static CharsetDirective parseCharset(String text) {
        return parseCharset(text, null);
    }

    public static CharsetDirective parseCharset(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        CharsetDirective result = parser.charsetDirective();
        parser.Expect(0);
        return result;
    }

    public static ClassDirective parseClass(String text) {
        return parseClass(text, null);
    }

    public static ClassDirective parseClass(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        ClassDirective result = parser.classDirective();
        parser.Expect(0);
        return result;
    }

    public static DefineDirective parseDefine(String text) {
        return parseDefine(text, null);
    }

    public static DefineDirective parseDefine(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        DefineDirective result = parser.defineDirective();
        parser.Expect(0);
        return result;
    }

    public static FontFaceDirective parseFontFace(String text) {
        return parseFontFace(text, null);
    }

    public static FontFaceDirective parseFontFace(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        FontFaceDirective result = parser.fontFaceDirective();
        parser.Expect(0);
        return result;
    }

    public static ImportDirective parseImport(String text) {
        return parseImport(text, null);
    }

    public static ImportDirective parseImport(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        ImportDirective result = parser.importDirective();
        parser.Expect(0);
        return result;
    }

    public static IncludeDirective parseInclude(String text) {
        return parseInclude(text, null);
    }

    public static IncludeDirective parseInclude(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        IncludeDirective result = parser.includeDirective();
        parser.Expect(0);
        return result;
    }

    public static NamespaceDirective parseNamespace(String text) {
        return parseNamespace(text, null);
    }

    public static NamespaceDirective parseNamespace(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        NamespaceDirective result = parser.namespaceDirective();
        parser.Expect(0);
        return result;
    }

    public static PageDirective parsePage(String text) {
        return parsePage(text, null);
    }

    public static PageDirective parsePage(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        PageDirective result = parser.pageDirective();
        parser.Expect(0);
        return result;
    }

    public static RuleSet parseRuleSet(String text) {
        return parseRuleSet(text, null);
    }

    public static RuleSet parseRuleSet(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        RuleSet result = parser.ruleset();
        parser.Expect(0);
        return result;
    }

    public static Rule parseRule(String text) {
        return parseRule(text, null);
    }

    public static Rule parseRule(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        Rule result = parser.rule();
        parser.Expect(0);
        return result;
    }

    public static Term parseTerm(String text) {
        return parseTerm(text, null);
    }

    public static Term parseTerm(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        Term result = parser.term();
        parser.Expect(0);
        return result;
    }

    public static Expression parseExpression(String text) {
        return parseExpression(text, null);
    }

    public static Expression parseExpression(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        Expression result = parser.expr();
        parser.Expect(0);
        return result;
    }

    public static Selector parseSelector(String text) {
        return parseSelector(text, null);
    }

    public static Selector parseSelector(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        Selector result = parser.selector();
        parser.Expect(0);
        return result;
    }

    public static Declaration parseDeclaration(String text) {
        return parseDeclaration(text, null);
    }

    public static Declaration parseDeclaration(String text, ErrorReporter errors) {
        Parser parser = getParser(text, errors);
        Declaration result = parser.declaration();
        parser.Expect(0);
        return result;
    }
}
