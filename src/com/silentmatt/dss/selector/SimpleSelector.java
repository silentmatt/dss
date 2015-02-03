package com.silentmatt.dss.selector;

import com.silentmatt.dss.Immutable;
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
@Immutable
public final class SimpleSelector {
    public static class Builder {
        private Combinator combinator = Combinator.None;
        private String elementName;
        private String id;
        private String className;
        private String pseudo;
        private Attribute attribute;
        private SimpleSelector.Builder child;

        public Builder setCombinator(Combinator combinator) {
            this.combinator = combinator;
            return this;
        }

        public Builder setElementName(String elementName) {
            this.elementName = elementName;
            return this;
        }

        public Builder setID(String id) {
            this.id = id;
            return this;
        }

        public Builder setClassName(String className) {
            this.className = className;
            return this;
        }

        public Builder setPseudo(String pseudo) {
            this.pseudo = pseudo;
            return this;
        }

        public Builder setAttribute(Attribute attribute) {
            this.attribute = attribute;
            return this;
        }

        public Builder setChild(SimpleSelector.Builder child) {
            this.child = child;
            return this;
        }

        public SimpleSelector build() {
            return new SimpleSelector(combinator, elementName, id, className, pseudo, attribute, child == null ? null : child.build());
        }
    }

    // XXX: There was FunctionTerm function after attributes, but it was never used.
    private final Combinator combinator;
    private final String elementName;
    private final String id;
    private final String className;
    private final String pseudo;
    private final Attribute attribute;
    private final SimpleSelector child;

    public SimpleSelector() {
        this.combinator = Combinator.None;
        this.elementName = null;
        this.id = null;
        this.className = null;
        this.pseudo = null;
        this.attribute = null;
        this.child = null;
    }

    public SimpleSelector(Combinator combinator, String elementName, String id, String className, String pseudo, Attribute attribute, SimpleSelector child) {
        this.combinator = combinator;
        this.elementName = elementName;
        this.id = id;
        this.className = className;
        this.pseudo = pseudo;
        this.attribute = attribute;
        this.child = child;
    }

    public SimpleSelector withCombinator(Combinator combinator) {
        return new SimpleSelector(combinator, getElementName(), getID(), getClassName(), getPseudo(), getAttribute(), getChild());
    }

    public SimpleSelector withPseudo(String psd) {
        return new SimpleSelector(getCombinator(), getElementName(), getID(), getClassName(), psd, getAttribute(), getChild());
    }

    public SimpleSelector withElementName(String name) {
        return new SimpleSelector(getCombinator(), name, getID(), getClassName(), getPseudo(), getAttribute(), getChild());
    }

    public SimpleSelector withID(String id) {
        return new SimpleSelector(getCombinator(), getElementName(), id, getClassName(), getPseudo(), getAttribute(), getChild());
    }

    public SimpleSelector withClassName(String className) {
        return new SimpleSelector(getCombinator(), getElementName(), getID(), className, getPseudo(), getAttribute(), getChild());
    }

    public SimpleSelector withAttribute(Attribute attr) {
        return new SimpleSelector(getCombinator(), getElementName(), getID(), getClassName(), getPseudo(), attr, getChild());
    }

    public SimpleSelector withChild(SimpleSelector child) {
        return new SimpleSelector(getCombinator(), getElementName(), getID(), getClassName(), getPseudo(), getAttribute(), child);
    }

    
    
    /**
     * Evaluate the SimpleSelector.
     *
     * @return An equivalent {@link CssSimpleSelector}.
     */
    public CssSimpleSelector evaluate() {
        CssSimpleSelector result = new CssSimpleSelector();
        result.setCombinator(CssCombinator.fromDss(combinator));
        result.setElementName(elementName);
        result.setID(id);
        result.setClassName(className);
        result.setPseudo(pseudo);
        result.setAttribute(attribute != null ? attribute.evaluate() : null);
        result.setChild(child != null ? child.evaluate() : null);
        return result;
    }

    /**
     * Gets the combinator that should appear between this simple selector and
     * the previous one.
     *
     * There is no DescendantOf Combinator, so null is used instead. Eventually,
     * that will change, since it's ugly and unnecessary.
     *
     * @return The {@link Combinator}.
     */
    public Combinator getCombinator() {
        return combinator;
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
     * Gets the ID part of the selector.
     *
     * @return The ID to match, without the leading "#".
     */
    public String getID() {
        return id;
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
     * Gets the pseudoselector.
     *
     * @return The pseudoselector, without the leading ":", but may include
     * arguments in parentheses.
     */
    public String getPseudo() {
        return pseudo;
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

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
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
