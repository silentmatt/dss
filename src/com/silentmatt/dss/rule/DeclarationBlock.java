package com.silentmatt.dss.rule;

import com.google.common.collect.ImmutableList;
import com.silentmatt.dss.Immutable;
import com.silentmatt.dss.bool.BooleanExpression;
import com.silentmatt.dss.css.CssDeclaration;
import com.silentmatt.dss.declaration.Declaration;
import com.silentmatt.dss.declaration.DeclarationList;
import com.silentmatt.dss.declaration.Expression;
import com.silentmatt.dss.directive.ClassDirective;
import com.silentmatt.dss.directive.DeclarationDirective;
import com.silentmatt.dss.directive.GenericDirective;
import com.silentmatt.dss.directive.RuleSetClass;
import com.silentmatt.dss.evaluator.EvaluationState;
import com.silentmatt.dss.selector.Combinator;
import com.silentmatt.dss.selector.Selector;
import com.silentmatt.dss.term.ClassReferenceTerm;
import com.silentmatt.dss.term.RuleSetClassReferenceTerm;
import com.silentmatt.dss.term.Term;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a block of declarations in a DSS document.
 *
 * DeclarationBlock is the part of a rule that is surrounded by brackets.
 * 
 * DeclarationBlocks are attached to {@link RuleSet}, {@link ClassDirective},
 * {@link DeclarationDirective}, and {@link GenericDirective} objects.
 *
 * @author Matthew Crumley
 */
@Immutable
public final class DeclarationBlock {
    public static class Builder {
        public Builder() {
        }
        
        public Builder(List<Declaration> decs, List<NestedRuleSet> rulesets) {
            declarations.addAll(decs);
            nestedRuleSets.addAll(rulesets);
        }

        private final List<Declaration> declarations = new ArrayList<>();
        private final ImmutableList.Builder<NestedRuleSet> nestedRuleSets = new ImmutableList.Builder<>();
        private final ImmutableList.Builder<Rule> rules = new ImmutableList.Builder<>();

        @Deprecated
        public List<Declaration> getDeclarations() {
            return declarations;
        }

        /**
        * Adds a Declaration to the end of the block.
        *
        * @param declaration The {@link Declaration} to add.
        */
        public Builder addDeclaration(Declaration declaration) {
            declarations.add(declaration);
            return this;
        }

        /**
        * Appends the Declarations in a list to the end of the block.
        *
        * @param declarations A {@link List} of {@link Declaration}s to add.
        */
        public Builder addDeclarations(List<Declaration> declarations) {
            this.declarations.addAll(declarations);
            return this;
        }

        /**
        * Adds a RuleSet inside the block.
        *
        * @param cb The {@link Combinator} to apply to the RuleSet's selectors.
        * @param nested The {@link RuleSet} to nest inside the block.
        */
        public Builder addNestedRuleSet(Combinator cb, RuleSet nested, BooleanExpression condition) {
            nestedRuleSets.add(new NestedRuleSet(cb, nested, condition));
            return this;
        }

        /**
        * Adds a NestedRuleSet inside the block.
        *
        * @param nested The {@link NestedRuleSet} to nest inside the block.
        */
        public Builder addNestedRuleSet(NestedRuleSet nested) {
            nestedRuleSets.add(nested);
            return this;
        }

        /**
        * Adds a list of NestedRuleSets inside the block.
        *
        * @param nested The List of {@link NestedRuleSet}s to nest inside the block.
        */
        public Builder addNestedRuleSets(List<NestedRuleSet> nested) {
            nestedRuleSets.addAll(nested);
            return this;
        }
        
        public Builder addRule(Rule rule) {
            this.rules.add(rule);
            return this;
        }
        
        public Builder addRules(List<Rule> rules) {
            this.rules.addAll(rules);
            return this;
        }

        public DeclarationBlock build() {
            return new DeclarationBlock(new DeclarationList(ImmutableList.copyOf(declarations)), nestedRuleSets.build(), rules.build());
        }
    }

    private final DeclarationList declarations;
    private final ImmutableList<NestedRuleSet> nestedRuleSets;
    private final ImmutableList<Rule> rules;

    /**
     * Constructs a DeclarationBlock, containing a list of {@link Declaration}s.
     *
     * @param declarations The Declarations to initialize the block with. Declarations
     * are copied from the list by reference, so later changes to the list will
     * not affect the DeclarationBlock and vice versa, but changes to the
     * Declarations themselves will be reflected in the block.
     */
    @SuppressWarnings("unchecked")
    public DeclarationBlock(DeclarationList declarations) {
        this.declarations = declarations;
        this.nestedRuleSets = ImmutableList.of();
        this.rules = ImmutableList.of();
    }

    /**
     * Constructs a DeclarationBlock, containing a list of {@link Declaration}s
     * and {@link NestedRuleSet}s.
     *
     * @param declarations The Declarations to initialize the block with. As with
     * {@link #DeclarationBlock(java.util.List), the Declarations are copied into
     * a new list.
     *
     * @param nested The NestedRuleSets to initialize the block with. Like the
     * declarations, nested RuleSets are copied into a new list.
     */
    public DeclarationBlock(DeclarationList declarations, ImmutableList<NestedRuleSet> nested, ImmutableList<Rule> rules) {
        this.declarations = declarations;
        this.nestedRuleSets = nested;
        this.rules = rules;
    }

    /**
     * Gets the list of Declarations in the block.
     *
     * @return The block's DeclarationList.
     */
    public DeclarationList getDeclarations() {
        return declarations;
    }

    /**
     * Evaluates the declarations, to convert them into CssDeclarations.
     *
     * @param state The current {@link EvaluationState}.
     *
     * @return A {@link List} of {@link CssDeclaration}s.
     */
    public List<CssDeclaration> getCssDeclarations(EvaluationState state) {
        List<CssDeclaration> result = new ArrayList<>();
        for (Declaration d : declarations) {
            result.add(new CssDeclaration(d.getName(), d.getExpression().evaluate(state, declarations), d.isImportant()));
        }
        return result;
    }

    /**
     * Gets a property value by name.
     *
     * @param name The name of the property to get.
     *
     * @return The property's value as an {@link Expression}, or null if it
     * doesn't exist.
     */
    public Expression getValue(String name) {
        Declaration declaration = getDeclaration(name);
        return (declaration != null) ? declaration.getExpression() : null;
    }

    /**
     * Gets a Declaration by name.
     *
     * @param name The property name to get.
     *
     * @return A {@link Declaration} with the specified name, or null if none exists.
     */
    public Declaration getDeclaration(String name) {
        for (Declaration d : declarations) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    /**
     * Gets the string representation of the declarations and nested rule sets in the block.
     * 
     * @param nesting The desired nesting level.
     *
     * @return The serialized form of the block without the surrounding brackets.
     *
     * @see #toString()
     * @see #toString(int)
     */
    public String innerString(int nesting) {
        String start = Rule.getIndent(nesting);
        StringBuilder txt = new StringBuilder("");

        for (Declaration dec : declarations) {
            txt.append("\n\t").append(start);
            txt.append(dec.toString());
            txt.append(";");
        }

        for (RuleSet rs : nestedRuleSets) {
            txt.append("\n");
            txt.append(rs.toString(nesting + 1));
        }

        return txt.toString();
    }

    /**
     * Gets a list of nested RuleSets.
     *
     * @return The {@link List} of {@link NestedRuleSet} objects.
     */
    public ImmutableList<NestedRuleSet> getNestedRuleSets() {
        return nestedRuleSets;
    }

    /**
     * Gets a list of nested rules.
     *
     * @return The {@link List} of {@link NestedRuleSet} objects.
     */
    public ImmutableList<Rule> getRules() {
        return rules;
    }

    /**
     * Gets the string representation of the block.
     *
     * The block will be at the outermost nesting level (no leading indentation).
     *
     * @return The serialized representation of the block, including the brackets.
     *
     * @see #toString(int)
     * @see #innerString(int)
     */
    @Override
    public String toString() {
        return toString(0);
    }

    /**
     * Gets the string representation of the block, at the specified nesting level.
     *
     * @param nesting The desired nesting level.
     *
     * @return The serialized representation of the block, including the brackets.
     *
     * @see #toString()
     * @see #innerString(int)
     */
    public String toString(int nesting) {
        String start = Rule.getIndent(nesting);
        StringBuilder txt = new StringBuilder();

        txt.append("{");
        txt.append(innerString(nesting + 1));
        txt.append("\n").append(start).append("}");

        return txt.toString();
    }

    private static boolean matches(Declaration declaration, String name) {
        return declaration.getName().equalsIgnoreCase(name);
    }

    private static Map<String, Expression> setArguments(EvaluationState state, ClassDirective clazz, DeclarationList args) {
        DeclarationList formalParameters = clazz.getParameters(args);

        Map<String, Expression> parameters = new HashMap<>();

        // Defaults
        for (Declaration param : formalParameters) {
            parameters.put(param.getName(), param.getExpression());
        }

        // Arguments
        int argNumber = 0;
        for (Declaration arg : args) {
            String paramName = arg.getName();
            if (paramName.isEmpty()) {
                if (argNumber < 0) {
                    state.getErrors().semanticError("Positional arguments cannot follow named arguments in " + clazz.getClassName());
                    return parameters;
                }
                if (argNumber < formalParameters.size()) {
                    paramName = formalParameters.get(argNumber).getName();
                }
                else {
                    state.getErrors().warning("Too many arguments to class '" + clazz.getClassName() + "'");
                    continue;
                }
                ++argNumber;
            }
            else {
                argNumber = -1;
            }

            if (parameters.containsKey(paramName)) {
                parameters.put(paramName, arg.getExpression());
            }
            else {
                state.getErrors().warning(clazz.getClassName() + " does not have a parameter '" + arg.getName() + "'");
            }
        }

        return parameters;
    }

    private static void addInheritedProperties(DeclarationBlock.Builder result, EvaluationState state, ClassDirective clazz, DeclarationList args) throws IOException {
        // Make a copy of the properties, to substitute parameters into
        ArrayList<Declaration> properties = new ArrayList<>();
        for (Declaration prop : clazz.getDeclarations(args)) {
            Boolean cond = prop.getCondition().evaluate(state);
            if (cond != null && cond) {
                properties.add(new Declaration(prop.getName(), prop.getExpression(), prop.isImportant(), BooleanExpression.TRUE));
            }
        }

        state.pushParameters(setArguments(state, clazz, args));
        try {
            for (int i = 0; i < properties.size(); i++) {
                Declaration dec = properties.get(i);
                properties.set(i, dec.substituteValues(state, DeclarationList.EMPTY, true, true)); // new DeclarationList(ImmutableList.copyOf(result.getDeclarations()))
            }

            for (int i = 0; i < properties.size(); i++) {
                Declaration declaration = properties.get(i);
                result.addDeclaration(declaration);
            }

            for (NestedRuleSet rs : clazz.getNestedRuleSets()) {
                Boolean cond = rs.getCondition().evaluate(state);
                if (cond != null && cond) {
                    result.addNestedRuleSet(rs.substituteValues(state).withCondition(BooleanExpression.TRUE));
                }
            }
        }
        finally {
            state.popParameters();
        }
    }

    private static ClassDirective lookupRuleSet(RuleSetClassReferenceTerm crt, List<List<RuleSet>> ruleSets) {
        String needle = crt.getSelector().toString();
        boolean found = false;

        ImmutableList.Builder<RuleSet> allRuleSets = ImmutableList.builder();

        for (List<RuleSet> rsList : ruleSets) {
            for (RuleSet rs : rsList) {
                for (Selector s : rs.getSelectors()) {
                    if (s.toString().equals(needle)) {
                        found = true;
                        allRuleSets.add(rs);
                    }
                }
            }
        }

        return found ? new RuleSetClass(allRuleSets.build()) : null;
    }

    private static void addInheritedProperties(DeclarationBlock.Builder result, EvaluationState state, Expression inherits) throws MalformedURLException, IOException {
        for (Term inherit : inherits.getTerms()) {
            ClassReferenceTerm crt;
            if (inherit instanceof ClassReferenceTerm) {
                crt = (ClassReferenceTerm) inherit;
            }
            else {
                // XXX: May want to split ClassReferenceTerm into SimpleCRT and ParameterizedCRT
                // so this doesn't need to create a new arguments list every time (SCRT would share one)
                crt = new ClassReferenceTerm(null, inherit.toString());
            }

            ClassDirective clazz;
            // TODO: Move this logic to EvaluationState?
            if (crt instanceof RuleSetClassReferenceTerm) {
                clazz = lookupRuleSet((RuleSetClassReferenceTerm) crt, state.getRuleSets());
            }
            else {
                clazz = state.getClasses().get(crt.getName());
            }

            if (clazz == null) {
                state.getErrors().semanticError("no such class: " + crt.getName());
                return;
            }

            addInheritedProperties(result, state, clazz, crt.getArguments());
        }
    }

    /**
     * Evaluates the block, and returns the result.
     *
     * @param state The current {@link EvaluationState}.
     * @param doCalculations true if calc(...) terms should be evaluated.
     *
     * @return The result of the evaluation.
     *
     * @throws IOException
     */
    public DeclarationBlock evaluateStyle(EvaluationState state, boolean doCalculations) throws IOException {
        DeclarationBlock.Builder result = new DeclarationBlock.Builder(new ArrayList<Declaration>(), new ArrayList<>(nestedRuleSets));
        return evaluateStyle(result, state, doCalculations);
    }

    protected List<RuleSet> getRuleSetScope() {
        List<RuleSet> result = new ArrayList<>(nestedRuleSets.size());
        for (NestedRuleSet nrs : nestedRuleSets) {
            result.add(nrs);
        }
        return result;
    }

    protected DeclarationBlock evaluateStyle(DeclarationBlock.Builder result, EvaluationState state, boolean doCalculations) throws IOException {
        state.pushScope(getRuleSetScope());
        try {
            for (Declaration declaration : getDeclarations()) {
                if (matches(declaration, "extend") || matches(declaration, "apply")) {
                    addInheritedProperties(result, state, declaration.getExpression());
                }
                else {
                    Boolean cond = declaration.getCondition().evaluate(state);
                    if (cond != null && cond) {
                        result.addDeclaration(declaration.withCondition(BooleanExpression.TRUE));
                    }
                }
            }

            for (int i = 0; i < result.getDeclarations().size(); i++) {
                Declaration dec = result.getDeclarations().get(i).substituteValues(state, getDeclarations(), false, doCalculations); // new DeclarationList(ImmutableList.copyOf(result.getDeclarations()))
                result.getDeclarations().set(i, dec);
            }
        }
        finally {
            state.popScope();
        }

        return result.build();
    }
}
