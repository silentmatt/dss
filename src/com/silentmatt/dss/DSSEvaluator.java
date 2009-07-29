package com.silentmatt.dss;

import com.silentmatt.dss.directive.PageDirective;
import com.silentmatt.dss.directive.MediaDirective;
import com.silentmatt.dss.directive.IncludeDirective;
import com.silentmatt.dss.directive.ClassDirective;
import com.silentmatt.dss.directive.FontFaceDirective;
import com.silentmatt.dss.directive.DefineDirective;
import com.silentmatt.dss.directive.Directive;
import com.silentmatt.dss.term.Term;
import com.silentmatt.dss.expression.CalcExpression;
import com.silentmatt.dss.expression.CalculationException;
import com.silentmatt.dss.expression.Value;
import com.silentmatt.dss.parser.ErrorReporter;
import com.silentmatt.dss.parser.PrintStreamErrorReporter;
import com.silentmatt.dss.term.CalculationTerm;
import com.silentmatt.dss.term.ClassReferenceTerm;
import com.silentmatt.dss.term.ConstTerm;
import com.silentmatt.dss.term.ParamTerm;
import com.silentmatt.dss.term.ReferenceTerm;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class DSSEvaluator {

    public static class Options {
        private URL baseURL;
        private ErrorReporter errors = new PrintStreamErrorReporter();
        private Scope<ClassDirective> classes = new Scope<ClassDirective>(null);
        private Scope<Expression> variables = new Scope<Expression>(null);

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

        public Scope<ClassDirective> getClasses() {
            return classes;
        }

        public void setClasses(Scope<ClassDirective> classes) {
            this.classes = classes;
        }

        public Scope<Expression> getVariables() {
            return variables;
        }

        public void setVariables(Scope<Expression> variables) {
            this.variables = variables;
        }
    }

    public class EvaluationState {
        private Deque<URL> baseURL;
        private ErrorReporter errors = new PrintStreamErrorReporter();
        private Scope<ClassDirective> classes = new Scope<ClassDirective>(null);
        private Scope<Expression> variables = new Scope<Expression>(null);
        private Scope<Expression> parameters;

        public EvaluationState(Options opts) {
            this.baseURL = new LinkedList<URL>();
            baseURL.push(opts.getBaseURL());
            this.errors = opts.getErrors();
            this.classes = new Scope<ClassDirective>(opts.getClasses());
            this.variables = new Scope<Expression>(opts.getVariables());
        }

        public URL getBaseURL() {
            return baseURL.getFirst();
        }

        public ErrorReporter getErrors() {
            return errors;
        }

        public Scope<ClassDirective> getClasses() {
            return classes;
        }

        public Scope<Expression> getVariables() {
            return variables;
        }

        public Scope<Expression> getParameters() {
            return parameters;
        }

        public void pushBaseURL(URL newBase) {
            baseURL.push(newBase);
            pushScope();
        }

        public void popBaseURL() {
            popScope();
            baseURL.pop();
        }

        public void pushScope() {
            classes = new Scope<ClassDirective>(classes);
            variables = new Scope<Expression>(variables);
        }

        public void popScope() {
            classes = classes.parent();
            variables = variables.parent();
        }

        public void pushParameters() {
            parameters = new Scope<Expression>(parameters);
        }

        public void popParameters() {
            parameters = parameters.parent();
        }
    }

    private final EvaluationState state;

    public DSSEvaluator(Options opts) {
        this.state = new EvaluationState(opts);
    }

    public void evaluate(CSSDocument css) throws MalformedURLException, IOException {
        state.pushScope();
        try {
            evaluateRules(css.getRules());
        }
        finally {
            state.popScope();
        }
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
            default:
                throw new IllegalStateException("Unknown rule type:" + rule.getRuleType());
            }
        }
    }

    private void evaluateDirective(Directive directive, List<Rule> container) throws MalformedURLException, IOException {
        switch (directive.getType()) {
            case Charset:
            case Namespace:
            case Import:
            case Other:
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
            case Include:
                evaluateInclude((IncludeDirective) directive, container);
                break;
            case Media:
                evaluateMediaDirective((MediaDirective) directive);
                break;
            case Page:
                evaluatePageDirective((PageDirective) directive);
                break;
            default:
                throw new IllegalStateException("Unknown Directive type:" + directive.getType());
        }
    }

    private void evaluateFontFaceDirective(FontFaceDirective rule) {
        evaluateStyle(rule.getDeclarations(), true);
    }

    private void evaluateMediaDirective(MediaDirective rule) throws MalformedURLException, IOException {
        state.pushScope();
        try {
            evaluateRules(rule.getRules());
        }
        finally {
            state.popScope();
        }
    }

    private void evaluatePageDirective(PageDirective rule) {
        evaluateStyle(rule.getDeclarations(), true);
    }

    private void evaluateRuleSet(RuleSet rule) throws MalformedURLException, IOException {
        state.pushScope();
        try {
            for (Directive dir : rule.getDirectives()) {
                evaluateDirective(dir, null);
            }
            evaluateStyle(rule.getDeclarations(), true);
        }
        finally {
            state.popScope();
        }
    }

    private void evaluateDefine(DefineDirective define, boolean global) {
        List<Declaration> properties = define.getDeclarations();
        evaluateStyle(properties, true);

        Scope<Expression> scope = state.getVariables();
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
        String className = cssClass.getClassName();
        evaluateStyle(cssClass.getDeclarations(), false);
        state.getClasses().declare(className, cssClass);
    }

    private void evaluateInclude(IncludeDirective rule, List<Rule> container) throws MalformedURLException, IOException {
        URL url = new URL(state.getBaseURL(), rule.getURLString());
        CSSDocument included = CSSDocument.parse(url.toString(), state.getErrors());
        if (included != null) {
            state.pushBaseURL(url);
            try {
                // Evaluate the first rule, since it's in the same index as the include
                if (included.getRules().size() > 0) {
                    evaluateRules(included.getRules().subList(0, 1));
                }
                rule.setIncludedDocument(included);
            }
            finally {
                state.popBaseURL();
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

    private void addInheritedProperties(List<Declaration> style, ClassReferenceTerm classReference) {
        String className = classReference.getName();
        ClassDirective clazz = state.getClasses().get(className);
        if (clazz == null) {
            state.getErrors().SemErr("no such class: " + className);
            return;
        }

        // Make a copy of the properties, to substitute parameters into
        List<Declaration> properties = new ArrayList<Declaration>(clazz.getDeclarations().size());
        for (Declaration prop : clazz.getDeclarations()) {
            Declaration copy = new Declaration();
            copy.setName(prop.getName());
            copy.setExpression(prop.getExpression());
            copy.setImportant(prop.isImportant());
            properties.add(copy);
        }

        // Fill in the parameter values
        state.pushParameters();
        try {
            // Defaults
            for (Declaration param : clazz.getParameters()) {
                state.getParameters().declare(param.getName(), param.getExpression());
            }
            // Arguments
            for (Declaration arg : classReference.getArguments()) {
                if (state.getParameters().declaresKey(arg.getName())) {
                    state.getParameters().put(arg.getName(), arg.getExpression());
                }
            }

            for (Declaration dec : properties) {
                substituteValue(dec, true, true);
            }
        }
        finally {
            state.popParameters();
        }

        inheritProperties(style, properties);
    }

    private void addInheritedProperties(List<Declaration> style, String className) {
        addInheritedProperties(style, new ClassReferenceTerm(className));
    }

    private void inheritProperties(List<Declaration> style, List<Declaration> properties) {
        for (int i = properties.size() - 1; i >= 0; i--) {
            Declaration declaration = properties.get(i);
            String property = declaration.getName();
            // Don't overwrite existing properties
            if (!hasProperty(style, property)) {
                style.add(0, declaration);
            }
        }
    }

    private void addInheritedProperties(List<Declaration> style, Expression inherits) {
        for (Term inherit : inherits.getTerms()) {
            if (inherit instanceof ClassReferenceTerm) {
                addInheritedProperties(style, (ClassReferenceTerm) inherit);
            }
            else {
                addInheritedProperties(style, inherit.toString());
            }
        }
    }

    private void substituteValue(Declaration property, boolean doCalculations) {
        substituteValue(property, false, doCalculations);
    }

    private void substituteValue(Declaration property, boolean withParams, boolean doCalculations) {
        Expression value = property.getExpression();
        Expression newValue = new Expression();

        for (Term primitiveValue : value.getTerms()) {
            if (primitiveValue instanceof ConstTerm || (withParams && primitiveValue instanceof ParamTerm)) {
                ReferenceTerm function = (ReferenceTerm) primitiveValue;
                Expression sub = function.evaluate(state);
                if (sub != null) {
                    for (Term t : sub.getTerms()) {
                        newValue.getTerms().add(t);
                    }
                }
            }
            else if (primitiveValue instanceof CalculationTerm) {
                CalcExpression expression = ((CalculationTerm) primitiveValue).getCalculation();
                // XXX: had "withParams ? state.getParameters() : null". Do we need a withParams flag?
                expression.substituteValues(state);
                if (doCalculations) {
                    Value calc = expression.calculateValue(state);
                    if (calc != null) {
                        try {
                            newValue.getTerms().add(calc.toTerm());
                        } catch (CalculationException ex) {
                            state.getErrors().SemErr(ex.getMessage());
                        }
                    }
                }
                else {
                    newValue.getTerms().add(primitiveValue);
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

    private void evaluateStyle(List<Declaration> style, boolean doCalculations) {
        state.pushScope();
        try {
            for (int i = 0; i < style.size(); i++) {
                Declaration property = style.get(i);
                if (property.getName().equals("extend")) {
                    // Add to inherits list
                    Expression inherit = property.getExpression();
                    addInheritedProperties(style, inherit);
                }
                else {
                    substituteValue(property, doCalculations);
                }
            }
            removeProperty(style, "extend");
        }
        finally {
            state.popScope();
        }
    }

}
