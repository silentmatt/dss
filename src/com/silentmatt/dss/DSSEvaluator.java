package com.silentmatt.dss;

import com.silentmatt.dss.directive.ClassDirective;
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

    public static class EvaluationState {
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
            Rule.evaluateRules(state, css.getRules());
        }
        finally {
            state.popScope();
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

    private static void addInheritedProperties(DSSEvaluator.EvaluationState state, List<Declaration> style, ClassReferenceTerm classReference) {
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
                DSSEvaluator.substituteValue(state, dec, true, true);
            }
        }
        finally {
            state.popParameters();
        }

        inheritProperties(style, properties);
    }

    private static void addInheritedProperties(DSSEvaluator.EvaluationState state, List<Declaration> style, String className) {
        addInheritedProperties(state, style, new ClassReferenceTerm(className));
    }

    private static void inheritProperties(List<Declaration> style, List<Declaration> properties) {
        for (int i = properties.size() - 1; i >= 0; i--) {
            Declaration declaration = properties.get(i);
            String property = declaration.getName();
            // Don't overwrite existing properties
            if (!hasProperty(style, property)) {
                style.add(0, declaration);
            }
        }
    }

    private static void addInheritedProperties(DSSEvaluator.EvaluationState state, List<Declaration> style, Expression inherits) {
        for (Term inherit : inherits.getTerms()) {
            if (inherit instanceof ClassReferenceTerm) {
                DSSEvaluator.addInheritedProperties(state, style, (ClassReferenceTerm) inherit);
            }
            else {
                DSSEvaluator.addInheritedProperties(state, style, inherit.toString());
            }
        }
    }

    private static void substituteValue(DSSEvaluator.EvaluationState state, Declaration property, boolean doCalculations) {
        substituteValue(state, property, false, doCalculations);
    }

    private static void substituteValue(DSSEvaluator.EvaluationState state, Declaration property, boolean withParams, boolean doCalculations) {
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

    public static void evaluateStyle(DSSEvaluator.EvaluationState state, List<Declaration> style, boolean doCalculations) {
        state.pushScope();
        try {
            for (int i = 0; i < style.size(); i++) {
                Declaration property = style.get(i);
                if (property.getName().equals("extend")) {
                    // Add to inherits list
                    Expression inherit = property.getExpression();
                    DSSEvaluator.addInheritedProperties(state, style, inherit);
                }
                else {
                    DSSEvaluator.substituteValue(state, property, doCalculations);
                }
            }
            removeProperty(style, "extend");
        }
        finally {
            state.popScope();
        }
    }

}
