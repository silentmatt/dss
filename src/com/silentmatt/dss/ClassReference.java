package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class ClassReference {
    private String name;
    private List<Declaration> arguments = new ArrayList<Declaration>();

    public ClassReference() {
    }

    public ClassReference(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }

    public List<Declaration> getArguments() {
        return arguments;
    }

    public void setArguments(List<Declaration> arguments) {
        this.arguments = arguments;
    }

    public void addArgument(Declaration argument) {
        arguments.add(argument);
    }

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
