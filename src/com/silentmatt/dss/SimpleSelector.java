package com.silentmatt.dss;

/**
 *
 * @author Matthew Crumley
 */
public class SimpleSelector {
    // XXX: There was FunctionTerm function after attributes, but it was never used.
    private Combinator combinator;
    private String elementName;
    private String id;
    private String className;
    private String pseudo;
    private Attribute attribute;
    private SimpleSelector child;

    public Combinator getCombinator() {
        return combinator;
    }

    public void setCombinator(Combinator Combinator) {
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

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute Attribute) {
        this.attribute = Attribute;
    }

    public SimpleSelector getChild() {
        return child;
    }

    public void setChild(SimpleSelector Child) {
        this.child = Child;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        if (combinator != null) {
            txt.append(combinator).append(' ');
        }
        if (elementName != null) { txt.append(elementName); }
        if (id != null) { txt.append("#").append(id); }
        if (className != null) { txt.append(".").append(className); }
        if (pseudo != null) { txt.append(":").append(pseudo); }
        if (attribute != null) { txt.append(attribute); }
        if (child != null) {
            if (child.elementName != null) { txt.append(" "); }
            txt.append(child);
        }
        return txt.toString();
    }
}
