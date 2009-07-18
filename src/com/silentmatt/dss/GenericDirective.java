package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author matt
 */
public class GenericDirective implements Directive {
    private List<Declaration> declarations = new ArrayList<Declaration>();
    private List<Rule> allRules = new ArrayList<Rule>();
    private List<RuleSet> ruleSet = new ArrayList<RuleSet>();
    private List<Directive> directives = new ArrayList<Directive>();
    private DirectiveType type;
    private String name;
    private List<Medium> mediums = new ArrayList<Medium>();
    private Expression expression;

    public List<RuleSet> getRuleSets() {
        return Collections.unmodifiableList(ruleSet);
    }

    public List<Directive> getDirectives() {
        return Collections.unmodifiableList(directives);
    }

    public void addRuleSet(RuleSet rs) {
        allRules.add(rs);
        ruleSet.add(rs);
    }

    public void addDirective(Directive dir) {
        allRules.add(dir);
        directives.add(dir);
    }

    public DirectiveType getType() {
        return type;
    }

    public void setType(DirectiveType type) {
        this.type = type;
    }

    public RuleType getRuleType() {
        return RuleType.Directive;
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
        return toString(0);
    }

    public String toString(int nesting) {
        String start = "";
        for (int i = 0; i < nesting; i++) {
            start += "\t";
        }

        StringBuilder txt = new StringBuilder();

        txt.append(name);
        txt.append(" ");

        if (getExpression() != null) {
            txt.append(getExpression().toString());
            txt.append(" ");
        }

        boolean first = true;
        for (Medium m : mediums) {
            if (first) {
                first = false;
                txt.append(" ");
            } else {
                txt.append(", ");
            }
            txt.append(m.toString());
        }

        boolean hasBlock = (this.declarations.size() > 0 || this.directives.size() > 0 || this.ruleSet.size() > 0);

        if (!hasBlock) {
            txt.append(";");
            return txt.toString();
        }

        txt.append(" {" + start);

        for (Directive dir : directives) {
            txt.append(dir.toString());
            txt.append("\n");
        }

        for (RuleSet rules : getRuleSets()) {
            txt.append(rules.toString(nesting + 1));
            txt.append("\n");
        }

        first = true;
        for (Declaration dec : declarations) {
            if (first) { first = false; } else { txt.append(";"); }
            txt.append("\n\t" + start);
            txt.append(dec.toString());
        }

        txt.append("\n" + start + "}");

        return txt.toString();
    }

    public String toCssString(int nesting) {
        return toString(nesting);
    }

}
