package com.silentmatt.dss;

import com.silentmatt.dss.DSSEvaluator.EvaluationState;
import com.silentmatt.dss.directive.Directive;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class RuleSet extends Rule {
    private List<Directive> directives = new ArrayList<Directive>();
    private List<Declaration> declarations = new ArrayList<Declaration>();
    private List<Selector> selectors = new ArrayList<Selector>();

    public List<Declaration> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(List<Declaration> declarations) {
        this.declarations = declarations;
    }

    public void addDeclaration(Declaration declaration) {
        declarations.add(declaration);
    }

    public List<Selector> getSelectors() {
        return selectors;
    }

    public void setSelectors(List<Selector> selectors) {
        this.selectors = selectors;
    }

    public List<Directive> getDirectives() {
        return directives;
    }

    public void setDirectives(List<Directive> directives) {
        this.directives = directives;
    }

    public void addDirective(Directive directive) {
        directives.add(directive);
    }

    public Declaration getDeclaration(String name) {
        for (Declaration d : declarations) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    public Expression getValue(String name) {
        Declaration declaration = getDeclaration(name);
        return declaration != null ? declaration.getExpression() : null;
    }

    @Override
    public String toString() {
        return toString(0);
    }

    public String toString(int nesting) {
        String start = Rule.getIndent(nesting);

        StringBuilder txt = new StringBuilder();
        boolean first = true;
        for (Selector sel : selectors) {
            if (first) {
                first = false;
                txt.append(start);
            }
            else {
                txt.append(", ");
            }
            txt.append(sel.toString());
        }
        txt.append(" {");

        for (Directive dir : directives) {
            txt.append("\n\t" + start);
            txt.append(dir.toString(nesting + 1));
        }
        for (Declaration dec : declarations) {
            txt.append("\n\t" + start);
            txt.append(dec.toString());
            txt.append(";");
        }

        txt.append("\n" + start + "}");

        return txt.toString();
    }

    public String toCssString(int nesting) {
        String start = Rule.getIndent(nesting);

        StringBuilder txt = new StringBuilder();
        boolean first = true;
        for (Selector sel : selectors) {
            if (first) {
                first = false;
                txt.append(start);
            }
            else {
                txt.append(", ");
            }
            txt.append(sel.toString());
        }
        txt.append(" {");

        for (Directive dir : directives) {
            String dirString = dir.toCssString(nesting + 1);
            if (dirString.length() > 0) {
                txt.append("\n\t" + start);
                txt.append(dirString);
            }
        }
        for (Declaration dec : declarations) {
            txt.append("\n\t" + start);
            txt.append(dec.toString());
            txt.append(";");
        }

        txt.append("\n" + start + "}");

        return txt.toString();
    }

    @Override
    public void evaluate(EvaluationState state, List<Rule> container) throws MalformedURLException, IOException {
        state.pushScope();
        try {
            for (Directive dir : this.getDirectives()) {
                dir.evaluate(state, null);
            }
            DSSEvaluator.evaluateStyle(state, this.getDeclarations(), true);
        }
        finally {
            state.popScope();
        }
    }
}
