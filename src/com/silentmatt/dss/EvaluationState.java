package com.silentmatt.dss;

import com.silentmatt.dss.directive.ClassDirective;
import java.net.URL;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author Matthew Crumley
 */
public final class EvaluationState {
    private final Deque<URL> baseURL;
    private final ErrorReporter errors;
    private Scope<ClassDirective> classes;
    private Scope<Expression> variables;
    private Scope<Expression> parameters = null;
    private final Map<String, Function> functions = new HashMap<String, Function>();

    public EvaluationState(DSSEvaluator.Options opts) {
        this.baseURL = new LinkedList<URL>();
        baseURL.push(opts.getBaseURL());
        this.errors = opts.getErrors();
        this.classes = new Scope<ClassDirective>(opts.getClasses());
        this.variables = new Scope<Expression>(opts.getVariables());
        this.functions.putAll(opts.getFunctions());
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

    public Map<String, Function> getFunctions() {
        return functions;
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
