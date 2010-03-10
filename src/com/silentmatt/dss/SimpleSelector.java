package com.silentmatt.dss;

import com.silentmatt.dss.css.CssCombinator;
import com.silentmatt.dss.css.CssSimpleSelector;

/**
 * A CSS simple selector.
 *
 * Each SimpleSelector matches an individual element. Contrary to their name, they
 * can be made up of multiple parts, like "a.someClass[href^=https\:]:active".
 *
 * The {@link Combinator}s joining SimpleSelectors are attached to the
 * SimpleSelector to the right.
 *
 * @todo This (like a few other classes, like Selector) could probably be a subclass
 * of the CSS version, since they are almost exactly the same.
 * 
 * @author Matthew Crumley
 */
public class SimpleSelector implements Cloneable {
    // XXX: There was FunctionTerm function after attributes, but it was never used.
    private Combinator combinator;
    private String elementName;
    private String id;
    private String className;
    private String pseudo;
    private Attribute attribute;
    private SimpleSelector child;

    /**
     * Evaluate the SimpleSelector.
     *
     * @return An equivalent {@link CssSimpleSelector}.
     */
    public CssSimpleSelector evaluate() {
        CssSimpleSelector result = new CssSimpleSelector();
        result.setCombinator(combinator != null ? CssCombinator.fromDss(combinator) : null);
        result.setElementName(elementName);
        result.setID(id);
        result.setClassName(className);
        result.setPseudo(pseudo);
        result.setAttribute(attribute != null ? attribute.evaluate() : null);
        result.setChild(child != null ? child.evaluate() : null);
        return result;
    }

    /**
     * Creates a new copy of this SimpleSelector.
     *
     * @return a new SimpleSelector that's equivalent to this one.
     */
    @Override
    public SimpleSelector clone() {
        SimpleSelector result = new SimpleSelector();
        result.setCombinator(combinator);
        result.setElementName(elementName);
        result.setID(id);
        result.setClassName(className);
        result.setPseudo(pseudo);
        result.setAttribute(attribute);
        result.setChild(child);
        return result;
    }

    /**
     * Gets the combinator that should appear between this simple selector and
     * the previous one.
     *
     * There is no DescendantOf Cominator, so null is used instead. Eventually,
     * that will change, since it's ugly and unnecessary.
     *
     * @return The {@link Combinator}.
     */
    public Combinator getCombinator() {
        return combinator;
    }

    /**
     * Sets the combinator that should appear between this simple selector and
     * the previous one.
     *
     * @param combinator The {@link Combinator}
     */
    public void setCombinator(Combinator combinator) {
        this.combinator = combinator;
    }

    /**
     * Gets the element name part of the selector.
     *
     * The element name can be an "*" to select any element.
     *
     * @return The element name part of the simple selector.
     */
    public String getElementName() {
        return elementName;
    }

    /**
     * Sets the element name.
     *
     * @param elementName The name of the element to match, or the "*" wildcard.
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /**
     * Gets the ID part of the selector.
     *
     * @return The ID to match, without the leading "#".
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the ID part of the selector.
     * 
     * @param id The ID to match, without the leading "#".
     */
    public void setID(String id) {
        this.id = id;
    }

    /**
     * Gets the class name part of the selector.
     *
     * @return The class name to match, without the leading ".".
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the class name part of the selector.
     *
     * @param id The class name to match, without the leading ".".
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets the pseudoselector.
     *
     * @return The pseudoselector, without the leading ":", but may include
     * arguments in parentheses.
     */
    public String getPseudo() {
        return pseudo;
    }

    /**
     * Sets the pseudoselector.
     *
     * @param pseudo The pseudoselector, without the leading ":".
     */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    /**
     * Gets the attribute condition part of the selector.
     *
     * This is the "[attr=value]" part.
     *
     * @return The {@link Attribute} condition.
     */
    public Attribute getAttribute() {
        return attribute;
    }

    /**
     * Sets the attribute condition part of the selector.
     *
     * @param Attribute An {@link Attribute} condition.
     */
    public void setAttribute(Attribute Attribute) {
        this.attribute = Attribute;
    }

    /**
     * Gets a child SimpleSelector.
     *
     * If there is more than one of some part of the simple selector, or the
     * order is not "normal", the SimpleSelector is broken up into the parent on
     * the left, and a child on the right (which may have its own children).
     *
     * @return The remaining part of the selector.
     */
    public SimpleSelector getChild() {
        return child;
    }

    /**
     * Set the child SimpleSelector.
     *
     * If you need a more complex selector than a single object allows, you can
     * add a child selector, which appends itself directly to the right of its
     * parent.
     *
     * @param Child The remaining part of the selector.
     */
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
            // This shouldn't happen if it was built by the parser, but deal with it anyway
            if (child.elementName != null) { txt.append(" "); }
            txt.append(child);
        }
        return txt.toString();
    }
}
