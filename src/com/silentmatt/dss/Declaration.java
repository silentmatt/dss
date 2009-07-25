package com.silentmatt.dss;

/**
 * Represents a DSS declaration (name-value pair).
 * Declarations are in this form: "name : term [,] term ... [!important]".
 *
 * Besides representing the basic CSS declarations, they are passed as arguments
 * to parameterized classes.
 *
 * @author Matthew Crumley
 */
public class Declaration {
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
}
