package com.silentmatt.dss;

/**
 *
 * @author Matthew Crumley
 */
public class Function {
    private String name;
    private Expression expression;

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
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
        txt.append(name).append("(");
        if (expression != null) {
            txt.append(expression.toString());
        }
        txt.append(")");
        return txt.toString();
    }
}
