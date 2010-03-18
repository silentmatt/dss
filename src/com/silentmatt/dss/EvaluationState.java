package com.silentmatt.dss;

import com.silentmatt.dss.directive.ClassDirective;
import java.net.URL;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Contains the current state of the {@link DSSEvaluator}.
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
    private final LinkedList<List<RuleSet>> ruleSetScope = new LinkedList<List<RuleSet>>();
    private final URLCallback includeCallback;

    /**
     * Constructs an EvalationState with specified options.
     *
     * @param opts A {@link DSSEvaluator.Options} object, used to initialize the state.
     */
    public EvaluationState(DSSEvaluator.Options opts) {
        this.baseURL = new LinkedList<URL>();
        baseURL.push(opts.getBaseURL());
        this.errors = opts.getErrors();
        this.classes = new Scope<ClassDirective>(opts.getClasses());
        this.variables = new Scope<Expression>(opts.getVariables());
        this.functions.putAll(opts.getFunctions());
        this.includeCallback = opts.getIncludeCallback();
    }

    /**
     * Gets the include callback.
     * 
     * @return The {@link URLCallback} to handle includes.
     */
    public URLCallback getIncludeCallback() {
        return this.includeCallback;
    }

    /**
     * Gets the current base URL.
     *
     * @return The absolute URL that relative URLs should be based on. This changes
     * as included documents are evaluated.
     */
    public URL getBaseURL() {
        return baseURL.getFirst();
    }

    /**
     * Gets the error reporter.
     *
     * @return The {@link ErrorReporter} instance used to report warnings or errors.
     */
    public ErrorReporter getErrors() {
        return errors;
    }

    /**
     * Gets the current class scope.
     *
     * @return The current {@link Scope} of {@link ClassDirective}s.
     */
    public Scope<ClassDirective> getClasses() {
        return classes;
    }

    /**
     * Gets the current variable scope.
     *
     * @return The current {@link Scope} of {@link Expression}s.
     */
    public Scope<Expression> getVariables() {
        return variables;
    }

    /**
     * Gets the current class parameter scope.
     *
     * This is null unless a class is currently being applied.
     * 
     * @return The current {@link Scope} of {@link Expression}s.
     */
    public Scope<Expression> getParameters() {
        return parameters;
    }

    /**
     * Gets a map of function names to functions.
     *
     * @return A {@link Map} of functions that should be applied by the evaluator.
     */
    public Map<String, Function> getFunctions() {
        return functions;
    }

    /**
     * Gets the RuleSets in the current and surrounding scopes.
     *
     * @return A {@link List} of {@link List}s of {@link RuleSet}s. Each inner
     * list is one level (from innermost to the outermost level order) of the
     * "scope chain", and contains all of the RuleSets in that level, in
     * document order.
     */
    public List<List<RuleSet>> getRuleSets() {
        return ruleSetScope;
    }

    /**
     * Sets a new URL as the current base, and creates a new scope.
     *
     * @param newBase The new base {@link URL}.
     * @param currentRuleSetScope The current list of {@link RuleSet}s being
     * evaluated.
     * 
     * @see #pushScope(java.util.List)
     */
    public void pushBaseURL(URL newBase, List<RuleSet> currentRuleSetScope) {
        baseURL.push(newBase);
        pushScope(currentRuleSetScope);
    }

    /**
     * Restores the previous base URL.
     */
    public void popBaseURL() {
        popScope();
        baseURL.pop();
    }

    /**
     * Creates a new level in the scope chain of classes, variables, and rule sets.
     *
     * @param currentRuleSetScope The current list of {@link RuleSet}s being
     * evaluated. It is added to the end of the RuleSet scope.
     */
    public void pushScope(List<RuleSet> currentRuleSetScope) {
        classes = new Scope<ClassDirective>(classes);
        variables = new Scope<Expression>(variables);
        ruleSetScope.addLast(currentRuleSetScope);
    }

    /**
     * Restore the previous scope.
     */
    public void popScope() {
        classes = classes.parent();
        variables = variables.parent();
        ruleSetScope.removeLast();
    }

    /**
     * Creates a new level in the parameter scope chain.
     */
    public void pushParameters() {
        parameters = new Scope<Expression>(parameters);
    }

    /**
     * Restores the previous parameter scope.
     */
    public void popParameters() {
        parameters = parameters.parent();
    }
}
