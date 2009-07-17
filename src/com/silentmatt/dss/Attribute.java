package com.silentmatt.dss;

/**
 *
 * @author matt
 */
public class Attribute {
    private String operand;
    private AttributeOperator operator;
    private String value;

    public String getOperand() {
        return operand;
    }

    public void setOperand(String Operand) {
        this.operand = Operand;
    }

    public AttributeOperator getOperator() {
        return operator;
    }

    public void setOperator(AttributeOperator Operator) {
        this.operator = Operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String Value) {
        this.value = Value;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        txt.append("[").append(operand);
        if (operator != null) {
            switch (operator) {
            case Equals: txt.append("="); break;
            case InList: txt.append("~="); break;
            case Hyphenated: txt.append("|="); break;
            case BeginsWith: txt.append("^="); break;
            case EndsWith: txt.append("$="); break;
            case Contains: txt.append("*="); break;
            }
            txt.append(value);
        }
        txt.append("]");
        return txt.toString();
    }
}
