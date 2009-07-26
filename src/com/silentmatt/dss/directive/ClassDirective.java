package com.silentmatt.dss.directive;

import com.silentmatt.dss.*;
import com.silentmatt.dss.directive.DeclarationDirective;
import com.silentmatt.dss.directive.DirectiveType;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class ClassDirective extends DeclarationDirective {
    private String id;
    private List<Declaration> parameters;

    public ClassDirective(String id, List<Declaration> parameters, List<Declaration> declarations) {
        super(declarations);
        setID(id);
        setParameters(parameters);
    }

    public String getName() {
        return "@class";
    }

    public DirectiveType getType() {
        return DirectiveType.Class;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public List<Declaration> getParameters() {
        return parameters;
    }

    public void setParameters(List<Declaration> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(Declaration param) {
        parameters.add(param);
    }

    @Override
    public String toString(int nesting) {
        String start = "";
        for (int i = 0; i < nesting; i++) {
            start += "\t";
        }

        StringBuilder txt = new StringBuilder(start);
        txt.append("@class ");
        txt.append(getID());
        if (parameters.size() > 0) {
            txt.append("<");
            boolean first = true;
            for (Declaration param : parameters) {
                if (!first) {
                    txt.append("; ");
                }
                first = false;
                txt.append(param.getExpression() == null ? param.getName() : param.toString());
            }
            txt.append(">");
        }
        txt.append(" ");
        txt.append(getDeclarationsString(nesting));
        return txt.toString();
    }

    public void addDeclartion(Declaration declaration) {
        getDeclarations().add(declaration);
    }

    @Override
    public String toCssString(int nesting) {
        return "";
    }
}
