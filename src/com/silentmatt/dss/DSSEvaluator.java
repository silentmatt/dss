package com.silentmatt.dss;

import com.silentmatt.dss.css.CssDocument;
import com.silentmatt.dss.directive.ClassDirective;
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

    public CssDocument evaluate(DSSDocument css) throws MalformedURLException, IOException {
        CssDocument document = new CssDocument();
        state.pushScope();
        try {
            document.getRules().addAll(Rule.evaluateRules(state, css.getRules()));
            return document;
        }
        finally {
            state.popScope();
        }
    }
}
