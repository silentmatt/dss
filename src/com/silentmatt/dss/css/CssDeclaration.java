package com.silentmatt.dss.css;

/**
 * Represents a CSS declaration (name-value pair).
 * Declarations are in this form: "name : term [,] term ... [!important]".
 *
 * Besides representing the basic CSS declarations, they are passed as arguments
 * to parameterized classes.
 *
 * @author Matthew Crumley
 */
public class CssDeclaration /*implements Map.Entry<String, CssExpression>*/ {
    /**
     * Default constructor.
     */
    public CssDeclaration() {
    }

    /**
     * Constructs a Declaration with a specified name and expression.
     */
    public CssDeclaration(String name, CssExpression expression) {
        this.name = name;
        this.expression = expression;
    }

    /**
     * Constructs a Declaration with a specified name, expression and important flag.
     */
    public CssDeclaration(String name, CssExpression expression, boolean important) {
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
    private CssExpression expression;

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
    public CssExpression getExpression() {
        return expression;
    }

    /**
     * Sets the expression (right side of the ':').
     *
     * @param CssExpression The value to assign to the property.
     */
    public void setExpression(CssExpression Expression) {
        this.expression = Expression;
    }

    /**
     * Gets the string form of the declaration.
     *
     * @return A String of the form "property-name: value [!important]".
     */
    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean compact) {
        StringBuilder txt = new StringBuilder();
        txt.append(name).append(":");
        if (!compact) {
            txt.append(' ');
        }
        txt.append(expression.toString(compact));
        if (important) {
            txt.append(" !important");
        }
        return txt.toString();
    }

//    public String getKey() {
//        return getName();
//    }
//
//    public CssExpression getValue() {
//        return getExpression();
//    }
//
//    public CssExpression setValue(CssExpression arg0) {
//        CssExpression old = getExpression();
//        setExpression(arg0);
//        return old;
//    }
}
