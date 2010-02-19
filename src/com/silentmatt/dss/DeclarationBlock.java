package com.silentmatt.dss;

import com.silentmatt.dss.css.CssDeclaration;
import com.silentmatt.dss.css.CssRule;
import com.silentmatt.dss.directive.ClassDirective;
import com.silentmatt.dss.term.ClassReferenceTerm;
import com.silentmatt.dss.term.Term;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class DeclarationBlock extends Rule {
    private final DeclarationList declarations;
    private final List<NestedRuleSet> nestedRuleSets;

    public DeclarationBlock() {
        this.declarations = new DeclarationList();
        this.nestedRuleSets = new ArrayList<NestedRuleSet>();
    }

    public DeclarationBlock(List<Declaration> declarations) {
        this.declarations = new DeclarationList(declarations);
        this.nestedRuleSets = new ArrayList<NestedRuleSet>();
    }

    public DeclarationBlock(List<Declaration> declarations, List<NestedRuleSet> nested) {
        this.declarations = new DeclarationList(declarations);
        this.nestedRuleSets = new ArrayList<NestedRuleSet>(nested);
    }

    public DeclarationList getDeclarations() {
        return declarations;
    }

    public List<CssDeclaration> getCssDeclarations(EvaluationState state) {
        List<CssDeclaration> result = new ArrayList<CssDeclaration>();
        for (Declaration d : declarations) {
            result.add(new CssDeclaration(d.getName(), d.getExpression().evaluate(state, declarations), d.isImportant()));
        }
        return result;
    }

    public void addDeclaration(Declaration declaration) {
        declarations.add(declaration);
    }

    public void addDeclarations(List<Declaration> declarations) {
        for (Declaration declaration : declarations) {
            this.declarations.add(declaration);
        }
    }

    public Expression getValue(String name) {
        Declaration declaration = getDeclaration(name);
        return (declaration != null) ? declaration.getExpression() : null;
    }

    public Declaration getDeclaration(String name) {
        for (Declaration d : declarations) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    @Deprecated
    public String getDeclarationsString(int nesting) {
        String start = Rule.getIndent(nesting);
        StringBuilder txt = new StringBuilder();

        txt.append("{");
        txt.append(innerString(nesting + 1));
        txt.append("\n" + start + "}");

        return txt.toString();
    }

    public String innerString(int nesting) {
        String start = Rule.getIndent(nesting);
        StringBuilder txt = new StringBuilder("");

        for (Declaration dec : declarations) {
            txt.append("\n\t" + start);
            txt.append(dec.toString());
            txt.append(";");
        }

        for (RuleSet rs : nestedRuleSets) {
            txt.append("\n");
            txt.append(rs.toString(nesting + 1));
        }

        return txt.toString();
    }

    public void addNestedRuleSet(Combinator cb, RuleSet nested) {
        nestedRuleSets.add(new NestedRuleSet(cb, nested));
    }

    public void addNestedRuleSet(NestedRuleSet nested) {
        nestedRuleSets.add(nested);
    }

    public List<NestedRuleSet> getNestedRuleSets() {
        return nestedRuleSets;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int nesting) {
        return getDeclarationsString(nesting);
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static boolean matches(Declaration declaration, String name) {
        return declaration.getName().equalsIgnoreCase(name);
    }

    private static void setArguments(EvaluationState state, ClassDirective clazz, DeclarationList args) {
        DeclarationList formalParameters = clazz.getParameters(args);

        // Defaults
        for (Declaration param : formalParameters) {
            state.getParameters().declare(param.getName(), param.getExpression());
        }

        // Arguments
        int argNumber = 0;
        for (Declaration arg : args) {
            String paramName = arg.getName();
            if (paramName.isEmpty()) {
                if (argNumber < 0) {
                    state.getErrors().SemErr("Positional arguments cannot follow named arguments in " + clazz.getName());
                    return;
                }
                if (argNumber < formalParameters.size()) {
                    paramName = formalParameters.get(argNumber).getName();
                }
                else {
                    state.getErrors().Warning("Too many arguments to class '" + clazz.getName() + "'");
                    continue;
                }
                ++argNumber;
            }
            else {
                argNumber = -1;
            }

            if (state.getParameters().declaresKey(paramName)) {
                state.getParameters().put(paramName, arg.getExpression());
            }
            else {
                state.getErrors().Warning(clazz.getName() + " does not have a parameter '" + arg.getName() + "'");
            }
        }
    }

    private static void addInheritedProperties(DeclarationBlock result, EvaluationState state, ClassDirective clazz, DeclarationList args) throws IOException {
        DeclarationList list = result.getDeclarations();
        // Make a copy of the properties, to substitute parameters into
        DeclarationList properties = new DeclarationList();
        for (Declaration prop : clazz.getDeclarations(args)) {
            properties.add(new Declaration(prop.getName(), prop.getExpression(), prop.isImportant()));
        }

        state.pushParameters();
        try {
            setArguments(state, clazz, args);

            for (Declaration dec : properties) {
                dec.substituteValue(state, list, true, true);
            }
        }
        finally {
            state.popParameters();
        }

        for (int i = 0; i < properties.size(); i++) {
            Declaration declaration = properties.get(i);
            list.add(declaration);
        }

        for (NestedRuleSet rs : clazz.getNestedRuleSets()) {
            result.addNestedRuleSet(rs);
        }
    }

    private static void addInheritedProperties(DeclarationBlock result, EvaluationState state, Expression inherits) throws MalformedURLException, IOException {
        for (Term inherit : inherits.getTerms()) {
            ClassReferenceTerm crt;
            if (inherit instanceof ClassReferenceTerm) {
                crt = (ClassReferenceTerm) inherit;
            }
            else {
                // XXX: May want to split ClassReferenceTerm into SimpleCRT and ParameterizedCRT
                // so this doesn't need to create a new arguments list every time (SCRT would share one)
                crt = new ClassReferenceTerm(inherit.toString());
            }

            ClassDirective clazz = state.getClasses().get(crt.getName());
            if (clazz == null) {
                state.getErrors().SemErr("no such class: " + crt.getName());
                return;
            }

            addInheritedProperties(result, state, clazz, crt.getArguments());
        }
    }

    public DeclarationBlock evaluateStyle(EvaluationState state, boolean doCalculations) throws IOException {
        DeclarationBlock result = new DeclarationBlock(new DeclarationList(), new ArrayList<NestedRuleSet>(nestedRuleSets));
        return evaluateStyle(result, state, doCalculations);
    }

    protected DeclarationBlock evaluateStyle(DeclarationBlock result, EvaluationState state, boolean doCalculations) throws IOException {
        state.pushScope();
        try {
            for (Declaration declaration : getDeclarations()) {
                if (matches(declaration, "extend") || matches(declaration, "apply")) {
                    addInheritedProperties(result, state, declaration.getExpression());
                }
                else {
                    result.addDeclaration(declaration);
                }
            }

            for (int i = 0; i < result.getDeclarations().size(); i++) {
                result.getDeclarations().get(i).substituteValue(state, result.getDeclarations(), false, doCalculations);
            }
        }
        finally {
            state.popScope();
        }

        return result;
    }
}
