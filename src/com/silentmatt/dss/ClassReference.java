package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 * A term that references a class with parameters.
 *
 * Example: centered<width: 100px>
 *
 * @author Matthew Crumley
 */
public class ClassReference {
    /**
     * The class name being referenced.
     */
    private String name;

    /**
     * The arguments to be passed to the class.
     */
    private List<Declaration> arguments = new ArrayList<Declaration>();

    /**
     * Constructs a ClassReference from a class name.
     *
     * @param name The name of the class to reference.
     *
     * @see #setName(java.lang.String)
     */
    public ClassReference(String name) {
        this.name = name;
    }

    /**
     * Gets the name of the class being referenced.
     *
     * @return The class name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the class being referenced.
     *
     * @param Name The class name.
     */
    public void setName(String Name) {
        this.name = Name;
    }

    /**
     * Gets the arguments to be passed to the class.
     * The List is shared, so changes to the returned List will affect the ClassReference.
     *
     * @return The CSS declarations that will be passed to the class.
     */
    public List<Declaration> getArguments() {
        return arguments;
    }

    /**
     * Sets the arguments to be passed to the class.
     * The List is shared, not copied, so any future changes to the List will
     * affect the ClassReference.
     *
     * @param arguments The CSS declarations that will be passed to the class.
     */
    public void setArguments(List<Declaration> arguments) {
        this.arguments = arguments;
    }

    /**
     * Adds an argument to the arguments list.
     *
     * The declaration will be appended to the current list of arguments.
     *
     * @param argument The CSS Declaration to append to the argument list.
     *
     * @see #setArguments(java.util.List)
     */
    public void addArgument(Declaration argument) {
        arguments.add(argument);
    }

    /**
     * Gets the DSS representation of the class reference.
     *
     * @return A String that looks like this: "class-name<name: value; other-name: some-value>"
     */
    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        txt.append(name).append("<");
        if (arguments != null) {
            boolean first = true;
            for (Declaration dec : arguments) {
                if (!first) {
                    txt.append("; ");
                }
                first = false;
                txt.append(dec.toString());
            }
        }
        txt.append(">");
        return txt.toString();
    }
}
