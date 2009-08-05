package com.silentmatt.dss;

import java.util.Map;

/**
 * Represents a DSS declaration (name-value pair).
 * Declarations are in this form: "name : term [,] term ... [!important]".
 *
 * Besides representing the basic CSS declarations, they are passed as arguments
 * to parameterized classes.
 *
 * @author Matthew Crumley
 */
public class Declaration implements Map.Entry<String, Expression> {
    /**
     * Default constructor.
     */
    public Declaration() {
    }

    /**
     * Constructs a Declaration with a specified name and expression.
     */
    public Declaration(String name, Expression expression) {
        this.name = name;
        this.expression = expression;
    }

    /**
     * Constructs a Declaration with a specified name, expression and important flag.
     */
    public Declaration(String name, Expression expression, boolean important) {
        this.name = name;
        this.expression = expression;
        this.important = important;
    }

    /**
     * The property name to the left of the ':'.
     */
    private String name;

    /**
     * The "!important" flag.
     */
    private boolean important;

    /**
     * The expression to the right of the ':'.
     */
    private Expression expression;

    /**
     * Gets the property name.
     *
     * @return The name (left side of the ':'.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the property name.
     *
     * @param Name The property name.
     */
    public void setName(String Name) {
        this.name = Name;
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
     * Turns the "!important" flag on or off.
     *
     * @param important Whether "!important" should be included or not.
     */
    public void setImportant(boolean important) {
        this.important = important;
    }

    /**
     * Gets the expression (right side of the ':').
     *
     * @return The value of the property being set.
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Sets the expression (right side of the ':').
     *
     * @param Expression The value to assign to the property.
     */
    public void setExpression(Expression Expression) {
        this.expression = Expression;
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

    public String getKey() {
        return getName();
    }

    public Expression getValue() {
        return getExpression();
    }

    public Expression setValue(Expression arg0) {
        Expression old = getExpression();
        setExpression(arg0);
        return old;
    }

    public void substituteValue(EvaluationState state, DeclarationList container, boolean withParams, boolean doCalculations) {
        Expression value = getExpression();
        Expression newValue = value.substituteValues(state, container, withParams, doCalculations);
        setExpression(newValue);
    }
}
