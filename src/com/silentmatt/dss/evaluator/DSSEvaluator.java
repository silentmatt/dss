package com.silentmatt.dss.evaluator;

import com.silentmatt.dss.DSSDocument;
import com.silentmatt.dss.css.CssDocument;
import com.silentmatt.dss.declaration.Expression;
import com.silentmatt.dss.directive.ClassDirective;
import com.silentmatt.dss.error.ErrorReporter;
import com.silentmatt.dss.error.PrintStreamErrorReporter;
import com.silentmatt.dss.rule.Rule;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the evaluation options and evaluation state to convert a {@link DSSDocument}
 * to a {@link CssDocument}.
 *
 * @author Matthew Crumley
 */
public class DSSEvaluator {

    /**
     * Stores options relating to the DSS evaluation.
     */
    public static class Options {
        private URL baseURL;
        private ErrorReporter errors = new PrintStreamErrorReporter();
        private Scope<ClassDirective> classes = new Scope<>(null);
        private Scope<Expression> variables = new Scope<>(null);
        private Map<String, Function> functions = new HashMap<>();
        private URLCallback includeCallback = null;
        private ResourceLocator resourceLocator = null;

        /**
         * Constructs an Options object for a given URL.
         *
         * @param url The URL of the DSS document. This becomes the base URL.
         */
        public Options(URL url) {
            baseURL = url;
            resourceLocator = new DefaultResourcesLocator();
        }

        /**
         * Sets a callback that gets called when a file is included.
         *
         * @param callback The callback handler.
         */
        public void setIncludeCallback(URLCallback callback) {
            this.includeCallback = callback;
        }

        /**
         * Gets the callback that will be called when a file is included.
         *
         * @return The callback handler.
         */
        public URLCallback getIncludeCallback() {
            return this.includeCallback;
        }

        /**
         * Gets the base URL of the document.
         *
         * @return The (virtual or real) URL of the document to be evalated.
         */
        public URL getBaseURL() {
            return baseURL;
        }

        /**
         * Sets the base URL for the document.
         *
         * The base URL is the URL that any relative URLs in the document are
         * relative to (e.g. for @include). It does not have to be the actual
         * URL of the DSS document.
         *
         * @param baseURL The (virtual or real) URL of the document to be evaluated.
         */
        public void setBaseURL(URL baseURL) {
            this.baseURL = baseURL;
        }

        /**
         * Sets the ResourceLocator used to find include files.
         *
         * @param callback The resource locator.
         */
        public void setResourceLocator(ResourceLocator resourceLocator) {
            this.resourceLocator = resourceLocator;
        }

        /**
         * Gets the ResourceLocator that will be used to find include files.
         *
         * @return The resource locator.
         */
        public ResourceLocator getResourceLocator() {
            return this.resourceLocator;
        }

        /**
         * Gets the {@link ErrorReporter} that will be used during evaluation.
         *
         * @return an {@link ErrorReporter} object.
         */
        public ErrorReporter getErrors() {
            return errors;
        }

        /**
         * Sets the {@link ErrorReporter} to be used during evaluation.
         *
         * This does not necessarily have to be the same object that was used
         * to parse the document.
         *
         * @param errors The {@link ErrorReporter} to use.
         */
        public void setErrors(ErrorReporter errors) {
            this.errors = errors;
        }

        /**
         * Gets the top-level DSS class scope.
         *
         * @return a {@link Scope} of {@link ClassDirective}s.
         */
        public Scope<ClassDirective> getClasses() {
            return classes;
        }

        /**
         * Sets the top-level DSS class scope.
         *
         * If you want to add classes to any that are already there, you will need
         * to declare them in the existing scope (see {@link #getClass()}). Calling
         * setClasses will replace the scope.
         *
         * @param classes The new top-level scope for DSS classes.
         */
        public void setClasses(Scope<ClassDirective> classes) {
            this.classes = classes;
        }

        /**
         * Gets the top-level variable scope.
         *
         * @return a {@link Scope} of {@link Expression}s.
         */
        public Scope<Expression> getVariables() {
            return variables;
        }

        /**
         * Sets the top-level variable scope.
         *
         * If you want to add variables to any that are already there, you will
         * need to declare them in the existing scope (see {@link #getVariables()}).
         * Calling setVariables will replace the scope.
         *
         * @param variables The new top-level variable scope.
         */
        public void setVariables(Scope<Expression> variables) {
            this.variables = variables;
        }

        /**
         * Gets a map of {@link Function}s.
         *
         * @return a {@link Map} from {@link String} to {@link Function}.
         */
        public Map<String, Function> getFunctions() {
            return functions;
        }

        /**
         * Sets the map of {@link Function}s.
         *
         * If you want to add functions to any that are already there, you will
         * need to add them in the existing function map (see {@link #getFunctions()}).
         * Calling setFunctions will replace the entire map.
         *
         * @param functions a {@link Map} from {@link String} to {@link Function}
         */
        public void setFunctions(Map<String, Function> functions) {
            this.functions = functions;
        }
    }

    private final EvaluationState state;

    /**
     * Constructs an evaluator with the given options.
     *
     * @param opts The {@link Options} to use for evaluation.
     */
    public DSSEvaluator(Options opts) {
        this.state = new EvaluationState(opts);
    }

    /**
     * Evaluates a DSS document.
     *
     * @param dss The document to evaluate
     *
     * @return The resulting {@link CssDocument}.
     *
     * @throws IOException if there is an error reading any included documents.
     *
     * @todo Should it just add I/O errors to the ErrorReporter?
     */
    public CssDocument evaluate(DSSDocument dss) throws IOException {
        CssDocument document = new CssDocument();
        state.pushScope(Rule.getRuleSets(dss.getRules()));
        try {
            document.getRules().addAll(Rule.evaluateRules(state, dss.getRules()));
            return document;
        }
        finally {
            state.popScope();
        }
    }
}
