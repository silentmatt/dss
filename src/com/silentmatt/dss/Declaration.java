package com.silentmatt.dss;

/**
 *
 * @author Matthew Crumley
 */
public class Declaration {
    private String name;
    private boolean important;
    private Expression expression;

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }

    public boolean isImportant() {
        return important;
    }

    public void setImportant(boolean Important) {
        this.important = Important;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression Expression) {
        this.expression = Expression;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        txt.append(name).append(": ").append(expression).append(important ? " !important" : "");
        return txt.toString();
    }
}
