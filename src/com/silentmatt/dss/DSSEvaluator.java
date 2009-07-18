package com.silentmatt.dss;

import com.silentmatt.dss.parser.ErrorReporter;
import com.silentmatt.dss.parser.PrintStreamErrorReporter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

public class DSSEvaluator {

    public static class Options {
        private URL baseURL;
        private ErrorReporter errors = new PrintStreamErrorReporter();

        public Options(URL url) {
            baseURL = url;
        }

        public URL getBaseURL() {
            return baseURL;
        }

        public void setBaseURL(URL baseURL) {
            this.baseURL = baseURL;
        }

        public ErrorReporter getErrors() {
            return errors;
        }

        public void setErrors(ErrorReporter errors) {
            this.errors = errors;
        }
    }

    private Options options;
    private Scope<ClassDirective> classes;
    private Scope<Expression> variables;

    public DSSEvaluator(Options opts) {
        this.options = opts;
        this.classes = new Scope<ClassDirective>(null);
        this.variables = new Scope<Expression>(null);
    }

    public void evaluate(CSSDocument css) throws MalformedURLException, IOException {
        classes = classes.inherit();
        variables = variables.inherit();

        evaluateRules(css.getRules());
    }

    private void evaluateRules(List<Rule> rules) throws MalformedURLException, IOException {
        for (int i = 0; i < rules.size(); i++) {
            Rule rule = rules.get(i);
            switch (rule.getRuleType()) {
            case Directive:
                evaluateDirective((Directive) rule, rules);
                break;
            case RuleSet:
                evaluateRuleSet((RuleSet) rule);
                break;
            }
        }
    }

    private void pushScope() {
        classes = new Scope<ClassDirective>(classes);
        variables = new Scope<Expression>(variables);
    }

    private void popScope() {
        classes = classes.parent();
        variables = variables.parent();
    }

    private void evaluateDirective(Directive directive, List<Rule> container) throws MalformedURLException, IOException {
        switch (directive.getType()) {
            case Charset:
                evaluateCharsetDirective((CharsetDirective) directive);
                break;
            case Class:
                evaluateClass((ClassDirective) directive);
                break;
            case Define:
                {
                    DefineDirective define = (DefineDirective) directive;
                    evaluateDefine(define, define.isGlobal());
                }
                break;
            case FontFace:
                evaluateFontFaceDirective((FontFaceDirective) directive);
                break;
            case Import:
                evaluateImportDirective((ImportDirective) directive);
                break;
            case Include:
                evaluateInclude((IncludeDirective) directive, container);
                break;
            case Media:
                evaluateMediaDirective((MediaDirective) directive);
                break;
            case Namespace:
                evaluateNamespaceDirective((NamespaceDirective) directive);
                break;
            case Page:
                evaluatePageDirective((PageDirective) directive);
                break;
            case Other:
                evaluateGenericDirective((GenericDirective) directive);
                break;
        }
    }

    private void evaluateCharsetDirective(CharsetDirective rule) {
        // Do nothing
        // XXX: should we support variables in @charset rules?
    }

    private void evaluateFontFaceDirective(FontFaceDirective rule) {
        evaluateStyle(rule.getDeclarations());
    }

    private void evaluateGenericDirective(GenericDirective rule) {
        // Do Nothing
    }

    private void evaluateNamespaceDirective(NamespaceDirective rule) {
        // Do Nothing
    }

    private void evaluateImportDirective(ImportDirective rule) {
        // Do Nothing
    }

    private void evaluateMediaDirective(MediaDirective rule) throws MalformedURLException, IOException {
        pushScope();
        try {
            evaluateRules(rule.getRules());
        }
        finally {
            popScope();
        }
    }

    private void evaluatePageDirective(PageDirective rule) {
        evaluateStyle(rule.getDeclarations());
    }

    private void evaluateRuleSet(RuleSet rule) throws MalformedURLException, IOException {
        pushScope();
        for (Directive dir : rule.getDirectives()) {
            evaluateDirective(dir, null);
        }
        evaluateStyle(rule.getDeclarations());
        popScope();
    }

    private void evaluateDefine(DefineDirective define, boolean global) {
        List<Declaration> properties = define.getDeclarations();
        evaluateStyle(properties);

        Scope<Expression> scope = variables;
        if (global) {
            while (scope.parent() != null) {
                scope = scope.parent();
            }
        }
        for (int i = 0; i < properties.size(); i++) {
            Declaration property = properties.get(i);
            scope.declare(property.getName(), property.getExpression());
        }
    }

    private void evaluateClass(ClassDirective cssClass) {
        String className = cssClass.getID();
        evaluateStyle(cssClass.getDeclarations());
        classes.declare(className, cssClass);
    }

    private void evaluateInclude(IncludeDirective rule, List<Rule> container) throws MalformedURLException, IOException {
        URL url = new URL(options.getBaseURL(), rule.getURLString());
        CSSDocument included = CSSDocument.parse(url.toString(), options.getErrors());
        if (included != null) {
            pushScope();
            URL baseURL = options.getBaseURL();
            try {
                // Evaluate the first rule, since it's in the same index as the include
                if (included.getRules().size() > 0) {
                    evaluateRules(included.getRules().subList(0, 1));
                }
                rule.setIncludedDocument(included);
            }
            finally {
                options.setBaseURL(baseURL);
                popScope();
            }

            int index = container.indexOf(rule);
            if (index != -1) {
                container.remove(index);
                for (Rule r : included.getRules()) {
                    container.add(index, r);
                    index++;
                }
            }
        }
    }

    private static boolean hasProperty(List<Declaration> style, String property) {
        for (Declaration dec : style) {
            if (dec.getName().equals(property)) {
                return true;
            }
        }
        return false;
    }

    private void addInheritedProperties(List<Declaration> style, String className) {
        ClassDirective clazz = classes.get(className);
        if (clazz == null) {
            options.errors.SemErr("no such class: " + className);
            return;
        }

        List<Declaration> properties = clazz.getDeclarations();

        for (int i = 0; i < properties.size(); i++) {
            Declaration declaration = properties.get(i);
            String property = declaration.getName();
            // Don't overwrite existing properties
            if (!hasProperty(style, property)) {
                style.add(declaration);
            }
        }
    }

    private void addInheritedProperties(List<Declaration> style, Expression inherits) {
        for (Term inherit : inherits.getTerms()) {
            addInheritedProperties(style, inherit.getValue());
        }
    }

    private Expression getConstantValue(String name) {
        return variables.get(name.trim());
    }

    private void substituteValue(Declaration property) {
        Expression value = property.getExpression();
        Expression newValue = new Expression();

        for (Term primitiveValue : value.getTerms()) {
            Function function = primitiveValue.getFunction();
            if (function != null && function.getName().equals("const")) {
                Expression sub = getConstantValue(function.getExpression().toString());
                if (sub != null) {
                    for (Term t : sub.getTerms()) {
                        newValue.getTerms().add(t);
                    }
                }
            }
            else {
                newValue.getTerms().add(primitiveValue);
            }
        }

        property.setExpression(newValue);
    }

    private static void removeProperty(List<Declaration> style, String property) {
        Iterator<Declaration> it = style.iterator();
        while (it.hasNext()) {
            Declaration dec = it.next();
            if (dec.getName().equals(property)) {
                it.remove();
            }
        }
    }

    private void evaluateStyle(List<Declaration> style) {
        pushScope();
        try {
            for (int i = 0; i < style.size(); i++) {
                Declaration property = style.get(i);
                if (property.getName().equals("extend")) {
                    // Add to inherits list
                    Expression inherit = property.getExpression();
                    addInheritedProperties(style, inherit);
                }
                else {
                    substituteValue(property);
                }
            }
            removeProperty(style, "extend");
        }
        finally {
            popScope();
        }
    }

}
