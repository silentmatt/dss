package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author matt
 */
public class GenericDirective implements Directive, RuleSetContainer, DirectiveContainer {
    private List<Declaration> declarations = new ArrayList<Declaration>();
    private List<RuleSet> ruleSet = new ArrayList<RuleSet>();
    private List<Directive> directives = new ArrayList<Directive>();
    private DirectiveType type;
    private String name;
    private List<Medium> mediums = new ArrayList<Medium>();
    private Expression expression;

    public List<RuleSet> getRuleSets() {
        return ruleSet;
    }

    public void setRuleSets(List<RuleSet> ruleSet) {
        this.ruleSet = ruleSet;
    }

    public List<Directive> getDirectives() {
        return directives;
    }

    public void setDirectives(List<Directive> directives) {
        this.directives = directives;
    }

    public DirectiveType getType() {
        return type;
    }

    public void setType(DirectiveType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Medium> getMediums() {
        return mediums;
    }

    public void setMediums(List<Medium> mediums) {
        this.mediums = mediums;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public List<Declaration> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(List<Declaration> declarations) {
        this.declarations = declarations;
    }

    @Override
    public String toString() {
        return toString(0, false);
    }

    public String toCompactString() {
        return toString(0, true);
    }

    public String toString(int nesting, boolean compact) {
        String start = "";
        if (!compact) {
            for (int i = 0; i < nesting; i++) {
                start += "\t";
            }
        }

        switch (type) {
            case Charset: return toCharSetString(start, compact);
            case Page: return toPageString(start, compact);
            case Media: return toMediaString(nesting, compact);
            case Import: return toImportString(compact);
            case FontFace: return toFontFaceString(start, compact);
        }

        StringBuilder txt = new StringBuilder();

        txt.append(name);
        if (!compact) {
            txt.append(" ");
        }

        if (getExpression() != null) {
            txt.append(compact ? getExpression().toCompactString() : getExpression().toString());
            if (!compact) {
                txt.append(" ");
            }
        }

        boolean first = true;
        for (Medium m : mediums) {
            if (first) {
                first = false;
                txt.append(" ");
            } else {
                txt.append(compact ? "," : ", ");
            }
            txt.append(m.toString());
        }

        boolean hasBlock = (this.declarations.size() > 0 || this.directives.size() > 0 || this.ruleSet.size() > 0);

        if (!hasBlock) {
            txt.append(";");
            return txt.toString();
        }

        txt.append((compact ? "{" : " {") + start);

        for (Directive dir : directives) {
            // FIXME:
            //txt.append(dir.toCharSetString(start + "\t", compact));
            if (!compact) {
                txt.append("\r\n");
            }
        }

        for (RuleSet rules : getRuleSets()) {
            txt.append(rules.toString(nesting + 1, compact));
            if (!compact) {
                txt.append("\r\n");
            }
        }

        first = true;
        for (Declaration dec : declarations) {
            if (first) { first = false; } else { txt.append(";"); }
            if (!compact) { txt.append("\r\n\t" + start); }
            txt.append(compact ? dec.toCompactString() : dec.toString());
        }

        txt.append(compact ? "}" : (start + "\t\r\n}"));

        return txt.toString();
    }

    private String toFontFaceString(String start, boolean compact) {
        StringBuilder txt = new StringBuilder();
        txt.append("@font-face");
        txt.append(compact ? "{" : " {");

        boolean first = true;
        for (Declaration dec : declarations) {
            if (first) { first = false; } else { txt.append(";"); }
            if (!compact) {
                txt.append("\r\n\t" + start);
                txt.append(dec.toString());
            }
            else {
                txt.append(dec.toCompactString());
            }
        }

        txt.append(compact ? "}" : "\r\n}");

        return txt.toString();
    }

    private String toImportString(boolean compact) {
        StringBuilder txt = new StringBuilder();
        txt.append("@import");
        if (!compact) { txt.append(" "); }

        if (getExpression() != null) {
            txt.append(compact ? getExpression().toCompactString() : getExpression().toString());
            if (!compact) txt.append(" ");
        }
        boolean first = true;
        for (Medium m : mediums) {
            if (first) {
                first = false;
                txt.append(" ");
            } else {
                txt.append(compact ? "," : ", ");
            }
            txt.append(m);
        }
        txt.append(";");
        return txt.toString();
    }

    private String toMediaString(int nesting, boolean compact) {
        StringBuilder txt = new StringBuilder();
        txt.append("@media");
        if (!compact) { txt.append(" "); }

        boolean first = true;
        for (Medium m : mediums) {
            if (first) {
                first = false;
            } else {
                txt.append(compact ? "," : ", ");
            }
            txt.append(m.toString());
        }
        txt.append(compact ? "{" : " {\r\n");

        for (RuleSet rules : ruleSet) {
            txt.append(rules.toString(nesting + 1, compact));
            if (!compact) {
                txt.append("\r\n");
            }
        }

        txt.append("}");
        return txt.toString();
    }

    private String toPageString(String start, boolean compact) {
        StringBuilder txt = new StringBuilder();
        txt.append("@page");
        if (!compact) { txt.append(" "); }

        if (getExpression() != null) {
            txt.append(compact ? getExpression().toCompactString() : getExpression().toString());
            if (!compact) {
                txt.append(" ");
            }
        }
        txt.append(compact ? "{" : "{\r\n");

        boolean first = true;
        for (Declaration dec : declarations) {
            if (first) { first = false; } else { txt.append(";"); }
            if (compact) { txt.append("\r\n\t" + start); }
            txt.append(compact ? dec.toCompactString() : dec.toString());
        }

        txt.append("}");
        return txt.toString();
    }

    private String toCharSetString(String start, boolean compact) {
        if (compact) {
            return name + " " + getExpression().toCompactString();
        }
        else {
            return start + name + " " + getExpression();
        }
    }
}
