package com.silentmatt.dss;

import com.silentmatt.dss.directive.ClassDirective;
import com.silentmatt.dss.term.Term;
import com.silentmatt.dss.expression.CalcExpression;
import com.silentmatt.dss.expression.CalculationException;
import com.silentmatt.dss.expression.Value;
import com.silentmatt.dss.term.CalculationTerm;
import com.silentmatt.dss.term.ClassReferenceTerm;
import com.silentmatt.dss.term.ConstTerm;
import com.silentmatt.dss.term.FunctionTerm;
import com.silentmatt.dss.term.ParamTerm;
import com.silentmatt.dss.term.ReferenceTerm;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DSSEvaluator {

    public static class Options {
        private URL baseURL;
        private ErrorReporter errors = new PrintStreamErrorReporter();
        private Scope<ClassDirective> classes = new Scope<ClassDirective>(null);
        private Scope<Expression> variables = new Scope<Expression>(null);
        private Map<String, Function> functions = new HashMap<String, Function>();

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

        public Map<String, Function> getFunctions() {
            return functions;
        }

        public void setFunctions(Map<String, Function> functions) {
            this.functions = functions;
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

    private static boolean hasProperty(DeclarationList style, String property) {
        return style.containsKey(property);
    }

    private static void addInheritedProperties(EvaluationState state, DeclarationList style, ClassReferenceTerm classReference) {
        String className = classReference.getName();
        ClassDirective clazz = state.getClasses().get(className);
        if (clazz == null) {
            state.getErrors().SemErr("no such class: " + className);
            return;
        }

        // Make a copy of the properties, to substitute parameters into
        DeclarationList properties = new DeclarationList();
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

    private static void addInheritedProperties(EvaluationState state, DeclarationList style, String className) {
        addInheritedProperties(state, style, new ClassReferenceTerm(className));
    }

    private static void inheritProperties(DeclarationList style, DeclarationList properties) {
        for (int i = properties.size() - 1; i >= 0; i--) {
            Declaration declaration = properties.get(i);
            String property = declaration.getName();
            // Don't overwrite existing properties
            if (!hasProperty(style, property)) {
                style.add(0, declaration);
            }
        }
    }

    private static void addInheritedProperties(EvaluationState state, DeclarationList style, Expression inherits) {
        for (Term inherit : inherits.getTerms()) {
            if (inherit instanceof ClassReferenceTerm) {
                DSSEvaluator.addInheritedProperties(state, style, (ClassReferenceTerm) inherit);
            }
            else {
                DSSEvaluator.addInheritedProperties(state, style, inherit.toString());
            }
        }
    }

    private static void substituteValue(EvaluationState state, Declaration property, boolean doCalculations) {
        substituteValue(state, property, false, doCalculations);
    }

    private static Expression substituteValues(EvaluationState state, Expression value, boolean withParams, boolean doCalculations) {
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
            else if (primitiveValue instanceof FunctionTerm) {
                FunctionTerm function = (FunctionTerm) primitiveValue;
                Expression argument = substituteValues(state, function.getExpression(), withParams, doCalculations);
                Expression result = new FunctionTerm(function.getName(), argument).applyFunction(state);
                if (result != null) {
                    newValue.getTerms().addAll(result.getTerms());
                }
                else {
                    newValue.getTerms().add(function);
                }
            }
            else {
                newValue.getTerms().add(primitiveValue);
            }
        }

        return newValue;
    }

    private static void substituteValue(EvaluationState state, Declaration property, boolean withParams, boolean doCalculations) {
        Expression value = property.getExpression();
        Expression newValue = DSSEvaluator.substituteValues(state, value, withParams, doCalculations);
        property.setExpression(newValue);
    }

    public static void evaluateStyle(EvaluationState state, DeclarationList style, boolean doCalculations) {
        state.pushScope();
        try {
            Expression inherit;
            while ((inherit = style.get("extend")) != null) {
                DSSEvaluator.addInheritedProperties(state, style, inherit);
                style.asMap().values().remove(inherit);
            }
            for (int i = 0; i < style.size(); i++) {
                Declaration property = style.get(i);
                DSSEvaluator.substituteValue(state, property, doCalculations);
            }
        }
        finally {
            state.popScope();
        }
    }

}
