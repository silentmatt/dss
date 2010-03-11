package com.silentmatt.dss.css;

/**
 *
 * @author Matthew Crumley
 */
public class CssSimpleSelector {
    // XXX: There was FunctionTerm function after attributes, but it was never used.
    private CssCombinator combinator = CssCombinator.Descendant;
    private String elementName;
    private String id;
    private String className;
    private String pseudo;
    private CssAttribute attribute;
    private CssSimpleSelector child;

    public CssCombinator getCombinator() {
        return combinator;
    }

    public void setCombinator(CssCombinator Combinator) {
        this.combinator = Combinator;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String ElementName) {
        this.elementName = ElementName;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public CssAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(CssAttribute Attribute) {
        this.attribute = Attribute;
    }

    public CssSimpleSelector getChild() {
        return child;
    }

    public void setChild(CssSimpleSelector Child) {
        this.child = Child;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean compact) {
        StringBuilder txt = new StringBuilder();
        txt.append(compact ? combinator.toCompactString() : combinator.toString());
        if (elementName != null) { txt.append(elementName); }
        if (id != null) { txt.append("#").append(id); }
        if (className != null) { txt.append(".").append(className); }
        if (pseudo != null) { txt.append(":").append(pseudo); }
        if (attribute != null) { txt.append(attribute); }
        if (child != null) {
            txt.append(child.toString(compact));
        }
        return txt.toString();
    }
}
