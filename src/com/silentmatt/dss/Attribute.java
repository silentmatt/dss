package com.silentmatt.dss;

import com.silentmatt.dss.css.CssAttribute;
import com.silentmatt.dss.css.CssAttributeOperator;

/**
 * Represents an attribute selector.
 * Attribute selectors look like this: "[attr=value]".
 *
 * @author Matthew Crumley
 */
@Immutable
public final class Attribute {
    public static class Builder {
        private String operand;
        private AttributeOperator operator;
        private String value;

        public void setOperand(String operand) {
            this.operand = operand;
        }

        public void setOperator(AttributeOperator operator) {
            this.operator = operator;
        }

        public void setValue(String Value) {
            this.value = Value;
        }
        
        public Attribute build() {
            return new Attribute(operand, operator, value);
        }
    }

    private final String operand;
    private final AttributeOperator operator;
    private final String value;

    public Attribute(String operand, AttributeOperator operator, String value) {
        this.operand = operand;
        this.operator = operator;
        this.value = value;
    }

    /**
     * Converts the attribute selector to a {@link CssAttribute} object.
     *
     * @return the equivalent CssAttribute object.
     */
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
     * Gets the selector's operator.
     *
     * @return An {@link AttributeOperator} representing the type of comparison.
     */
    public AttributeOperator getOperator() {
        return operator;
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
