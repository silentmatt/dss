package com.silentmatt.dss.directive;

import com.silentmatt.dss.*;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class ClassDirective extends DeclarationDirective {
    private String className;
    private List<Declaration> parameters;

    public ClassDirective(String className, List<Declaration> parameters, List<Declaration> declarations) {
        super(declarations);
        this.className = className;
        this.parameters = parameters;
    }

    public String getName() {
        return "@class";
    }

    public DirectiveType getType() {
        return DirectiveType.Class;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String id) {
        this.className = id;
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
        String start = Rule.getIndent(nesting);

        StringBuilder txt = new StringBuilder(start);
        txt.append("@class ");
        txt.append(getClassName());
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
