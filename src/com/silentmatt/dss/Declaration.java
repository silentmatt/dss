package com.silentmatt.dss;

import com.silentmatt.dss.bool.BooleanExpression;
import java.util.Map;

/**
 * Represents a DSS declaration (name-value pair).
 * Declarations are in this form: "name : term [, or /] term ... [!important]".
 *
 * Besides representing the basic CSS declarations, they are passed as arguments
 * to parameterized classes.
 *
 * @author Matthew Crumley
 */
@Immutable
public final class Declaration implements Map.Entry<String, Expression> {

    public static class Builder {
        private BooleanExpression condition;
        String name;
        Expression expression;
        boolean important;
        
        public String getName() {
            return name;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder setExpression(Expression expr) {
            this.expression = expr;
            return this;
        }

        public Builder setImportant(boolean important) {
            this.important = important;
            return this;
        }

        public Builder setCondition(BooleanExpression condition) {
            this.condition = condition;
            return this;
        }
        
        public Declaration build() {
            return new Declaration(name, expression, important, condition);
        }
    }

    /**
     * Constructs a Declaration with a specified name and expression.
     */
    public Declaration(String name, Expression expression) {
        this.name = name;
        this.expression = expression;
        this.important = false;
        this.condition = BooleanExpression.TRUE;
    }

    /**
     * Constructs a Declaration with a specified name, expression, and condition.
     */
    public Declaration(String name, Expression expression, BooleanExpression condition) {
        this.name = name;
        this.expression = expression;
        this.important = false;
        this.condition = condition != null ? condition : BooleanExpression.TRUE;
    }

    /**
     * Constructs a Declaration with a specified name, expression and important flag.
     */
    public Declaration(String name, Expression expression, boolean important) {
        this.name = name;
        this.expression = expression;
        this.important = important;
        this.condition = BooleanExpression.TRUE;
    }

    /**
     * Constructs a Declaration with a specified name, expression, important flag, and condition.
     */
    public Declaration(String name, Expression expression, boolean important, BooleanExpression condition) {
        this.name = name;
        this.expression = expression;
        this.important = important;
        this.condition = condition != null ? condition : BooleanExpression.TRUE;
    }

    /**
     * The property name to the left of the ':'.
     */
    private final String name;

    /**
     * The "!important" flag.
     */
    private final boolean important;

    /**
     * The expression to the right of the ':'.
     */
    private final Expression expression;

    /**
     * The condition
     */
    private final BooleanExpression condition;

    /**
     * Gets the property name.
     *
     * @return The name (left side of the ':').
     */
    public String getName() {
        return name;
    }

    /**
     * Whether the "!important" flag was specified.
     *
     * @return true if the declaration is "important", false otherwise.
     */
    public boolean isImportant() {
        return important;
    }

    /**
     * Gets the expression (right side of the ':').
     *
     * @return The expression.
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Gets the condition.
     *
     * @return The condition.
     */
    public BooleanExpression getCondition() {
        return condition;
    }

    /**
     * Gets the string form of the declaration.
     *
     * @return A String of the form "property-name: value [!important]".
     */
    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        txt.append(name).append(": ").append(expression).append(important ? " !important" : "");
        return txt.toString();
    }

    /**
     * Same as {@link #getName()}.
     *
     * This method is to allow Declarations to be used as {@link Map} entries.
     *
     * @return The property name.
     */
    @Override
    public String getKey() {
        return getName();
    }

    /**
     * Same as {@link #getValue()}.
     *
     * This method is to allow Declarations to be used as {@link Map} entries.
     *
     * @return The value of the property.
     */
    @Override
    public Expression getValue() {
        return getExpression();
    }

    /**
     * Throws an {@link UnsupportedOperationException} to disallow modification in a {@link Map}.
     *
     * @param arg0 Ignored
     *
     * @return Nothing
     *
     * @throws UnsupportedOperationException
     */
    @Override
    public Expression setValue(Expression arg0) {
        throw new UnsupportedOperationException();
    }

    protected Declaration substituteValues(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        Expression newValue = getExpression().substituteValues(state, container, withParams, doCalculations);
        return new Declaration(getName(), newValue, important, condition);
    }

    public Declaration withCondition(BooleanExpression condition) {
        return new Declaration(name, expression, important, condition);
    }
}
