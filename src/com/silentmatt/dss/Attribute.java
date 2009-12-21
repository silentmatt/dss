package com.silentmatt.dss;

import com.silentmatt.dss.css.CssAttribute;
import com.silentmatt.dss.css.CssAttributeOperator;

/**
 * Represents an attribute selector.
 * Attribute selectors look like this: "[attr=value]".
 *
 * @author Matthew Crumley
 */
public class Attribute {
    private String operand;
    private AttributeOperator operator;
    private String value;

    public CssAttribute evaluate() {
        CssAttribute result = new CssAttribute();
        result.setOperand(operand);
        result.setOperator(CssAttributeOperator.fromDss(operator));
        result.setValue(value);
        return result;
    }
    /**
     * Gets the operand (attribute name).
     *
     * @return The attribute name of the selector.
     */
    public String getOperand() {
        return operand;
    }

    /**
     * Sets the operand (attribute name).
     *
     * @param Operand The attribute name to be compared.
     */
    public void setOperand(String operand) {
        this.operand = operand;
    }

    /**
     * Gets the selector's operator.
     *
     * @return An {@link AttributeOperator} representing the type of comparison.
     */
    public AttributeOperator getOperator() {
        return operator;
    }

    /**
     * Sets the selector's operator.
     *
     * @param operator An {@link AttributeOperator} specifying the type of comparison.
     */
    public void setOperator(AttributeOperator operator) {
        this.operator = operator;
    }

    /**
     * Gets the value to compare the attribute to.
     *
     * @return The String being compared to the attribute.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value to compare the attribute to.
     *
     * @param Value The String to compare the attribute to.
     */
    public void setValue(String Value) {
        this.value = Value;
    }

    /**
     * Get the CSS selector text for the attribute selector.
     *
     * @return A string of the form "[attribute{=,~=,|=,^=,$=,*=}value]".
     */
    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder("[");
        txt.append(operand);
        if (operator != null) {
            txt.append(operator).append(value);
        }
        txt.append("]");
        return txt.toString();
    }
}
