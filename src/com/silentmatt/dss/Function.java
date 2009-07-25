package com.silentmatt.dss;

/**
 * A function "call" term.
 *
 * "Functions" include rgb(...), const(name), param(name), etc. but <strong>not</strong> url(...) or calc(...).
 * @todo const and param should probably be separated from functions like calc and url are.
 *
 * @author Matthew Crumley
 */
public class Function {
    /**
     * The function name.
     */
    private String name;

    /**
     * The parameters to the function.
     */
    private Expression expression;

    /**
     * Gets the name of the function.
     *
     * @return The referenced function's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the function.
     *
     * @param Name The referenced function's name.
     */
    public void setName(String Name) {
        this.name = Name;
    }

    /**
     * Gets the expression that is passed to the function.
     *
     * @return The function parameter expression.
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Sets the expression that is passed to the function.
     *
     * @param Expression The function parameter expression.
     */
    public void setExpression(Expression Expression) {
        this.expression = Expression;
    }

    /**
     * Gets the function term as a String.
     *
     * @return A String of the form "function(expression)".
     */
    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        txt.append(name).append("(");
        if (expression != null) {
            txt.append(expression.toString());
        }
        txt.append(")");
        return txt.toString();
    }
}
