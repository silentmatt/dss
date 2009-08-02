package com.silentmatt.dss.term;

import com.silentmatt.dss.Declaration;
import com.silentmatt.dss.DeclarationList;

/**
 * A term that references a class with parameters.
 *
 * Example: centered<width: 100px>
 *
 * @author Matthew Crumley
 */
public class ClassReferenceTerm extends Term {
    /**
     * The class name being referenced.
     */
    private final String name;

    /**
     * The arguments to be passed to the class.
     */
    private final DeclarationList arguments = new DeclarationList();

    /**
     * Constructs a ClassReference from a class name.
     *
     * @param name The name of the class to reference.
     *
     * @see #setName(java.lang.String)
     */
    public ClassReferenceTerm(String name) {
        super();
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
     * Gets the arguments to be passed to the class.
     * The List is shared, so changes to the returned List will affect the ClassReference.
     *
     * @return The CSS declarations that will be passed to the class.
     */
    public DeclarationList getArguments() {
        return arguments;
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
