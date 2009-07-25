package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 * @todo Remove this class, and just construct the directives directly (pun not intended).
 * @author Matthew Crumley
 */
public class DirectiveBuilder {
    private List<Rule> allRules = new ArrayList<Rule>();
    private List<Declaration> declarations = new ArrayList<Declaration>();
    private List<RuleSet> ruleSets = new ArrayList<RuleSet>();
    private List<Directive> directives = new ArrayList<Directive>();
    private DirectiveType type;
    private String name;
    private Expression expression;
    private List<Medium> mediums = new ArrayList<Medium>();
    private String id;
    private String url;
    private SimpleSelector ss;
    private List<Declaration> parameters = new ArrayList<Declaration>();

    public List<Declaration> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(List<Declaration> declarations) {
        this.declarations = declarations;
    }

    public void addDeclaration(Declaration declaration) {
        this.declarations.add(declaration);
    }

    public List<RuleSet> getRuleSets() {
        return ruleSets;
    }

    public void setRuleSets(List<RuleSet> ruleSets) {
        this.ruleSets = ruleSets;
    }

    public void addRuleSet(RuleSet ruleSet) {
        this.allRules.add(ruleSet);
        this.ruleSets.add(ruleSet);
    }

    public List<Directive> getDirectives() {
        return directives;
    }

    public void setDirectives(List<Directive> directives) {
        this.directives = directives;
    }

    public void addDirective(Directive directive) {
        this.allRules.add(directive);
        this.directives.add(directive);
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

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public List<Medium> getMediums() {
        return mediums;
    }

    public void setMediums(List<Medium> mediums) {
        this.mediums = mediums;
    }

    public Declaration getDeclaration(String name) {
        for (Declaration d : declarations) {
            if (d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public SimpleSelector getSimpleSelector() {
        return ss;
    }

    public void setSimpleSelector(SimpleSelector ss) {
        this.ss = ss;
    }

    public void addParameter(Declaration param) {
        parameters.add(param);
    }

    public Directive build() {
        switch (type) {
            case Charset:
                return buildCharsetDirective();
            case Class:
                return buildClassDirective();
            case Define:
                return buildDefineDirective();
            case FontFace:
                return buildFontFaceDirective();
            case Import:
                return buildImportDirective();
            case Include:
                return buildIncludeDirective();
            case Media:
                return buildMediaDirective();
            case Namespace:
                return buildNamespaceDirective();
            case Other:
                return buildGenericDirective();
            case Page:
                return buildPageDirective();
            default:
                throw new RuntimeException("Invalid directive type");
        }
    }

    public CharsetDirective buildCharsetDirective() {
        return new CharsetDirective(expression);
    }

    public ClassDirective buildClassDirective() {
        return new ClassDirective(id, parameters, declarations);
    }

    public DefineDirective buildDefineDirective() {
        return new DefineDirective(declarations, id != null && id.equals("global"));
    }

    public FontFaceDirective buildFontFaceDirective() {
        return new FontFaceDirective(declarations);
    }

    public Directive buildGenericDirective() {
        GenericDirective d = new GenericDirective();
        d.setType(type);
        d.setName(this.name);
        for (Rule r : allRules) {
            switch (r.getRuleType()) {
            case Directive:
                d.addDirective((Directive) r);
                break;
            case RuleSet:
                d.addRuleSet((RuleSet) r);
                break;
            }
        }
        d.setDeclarations(declarations);
        d.setExpression(expression);
        d.setMediums(mediums);
        return d;
    }

    public ImportDirective buildImportDirective() {
        Medium m = Medium.all;
        if (mediums.size() > 0) {
            m = mediums.get(0);
        }
        return new ImportDirective(expression, m);
    }

    public IncludeDirective buildIncludeDirective() {
        return new IncludeDirective(expression);
    }

    public MediaDirective buildMediaDirective() {
        return new MediaDirective(mediums, allRules);
    }

    public NamespaceDirective buildNamespaceDirective() {
        return new NamespaceDirective(id, expression);
    }

    public PageDirective buildPageDirective() {
        return new PageDirective(ss, declarations);
    }
}
