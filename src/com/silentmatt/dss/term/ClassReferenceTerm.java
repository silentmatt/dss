package com.silentmatt.dss.term;

import com.google.common.collect.ImmutableList;
import com.silentmatt.dss.Declaration;
import com.silentmatt.dss.DeclarationList;
import com.silentmatt.dss.Immutable;
import java.util.List;

/**
 * A term that references a class with parameters.
 *
 * Example: centered<width: 100px>
 *
 * @author Matthew Crumley
 */
@Immutable
public class ClassReferenceTerm extends Term {
    public static class Builder {
        private final String name;
        private final ImmutableList.Builder<Declaration> arguments = ImmutableList.builder();

        public Builder(String name) {
            this.name = name;
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
        
        public ClassReferenceTerm build() {
            return new ClassReferenceTerm(null, name, new DeclarationList(arguments.build())); // FIXME: Missing term separator
        }
    }

    /**
     * The class name being referenced.
     */
    private final String name;

    /**
     * The arguments to be passed to the class.
     */
    private final DeclarationList arguments;

    /**
     * Constructs a ClassReference from a class name and arguments.
     *
     * @param sep The separator
     * @param name The name of the class to reference.
     *
     * @see #setName(java.lang.String)
     */
    public ClassReferenceTerm(Character sep, String name, DeclarationList args) {
        super(sep);
        this.name = name;
        this.arguments = args;
    }

    /**
     * Constructs a ClassReference from a class name.
     *
     * @param sep The separator
     * @param name The name of the class to reference.
     *
     * @see #setName(java.lang.String)
     */
    public ClassReferenceTerm(Character sep, String name) {
        super(sep);
        this.name = name;
        this.arguments = DeclarationList.EMPTY;
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

    @Override
    public ClassReferenceTerm withSeparator(Character separator) {
        return new ClassReferenceTerm(separator, name, arguments);
    }
}
