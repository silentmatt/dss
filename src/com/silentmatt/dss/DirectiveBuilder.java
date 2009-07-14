package com.silentmatt.dss;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author matt
 */
public class DirectiveBuilder {
    private List<Declaration> declarations = new ArrayList<Declaration>();
    private List<RuleSet> ruleSet = new ArrayList<RuleSet>();
    private List<Directive> directives = new ArrayList<Directive>();
    private DirectiveType type;
    private String name;
    private Expression expression;
    private List<Medium> mediums = new ArrayList<Medium>();
    private String id;
    private String url;
    private SimpleSelector ss;

    public List<Declaration> getDeclarations() {
        return declarations;
    }

    public void setDeclarations(List<Declaration> declarations) {
        this.declarations = declarations;
    }

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
        CharsetDirective d = new CharsetDirective();
        d.setExpression(expression);
        return d;
    }

    public ClassDirective buildClassDirective() {
        ClassDirective d = new ClassDirective();
        d.setID(id);
        d.setDeclarations(declarations);
        return d;
    }

    public DefineDirective buildDefineDirective() {
        DefineDirective d = new DefineDirective();
        d.setDeclarations(declarations);
        return d;
    }

    public FontFaceDirective buildFontFaceDirective() {
        FontFaceDirective d = new FontFaceDirective();
        d.setDeclarations(declarations);
        return d;
    }

    public Directive buildGenericDirective() {
        GenericDirective d = new GenericDirective();
        d.setType(type);
        d.setName(this.name);
        d.setDeclarations(declarations);
        d.setDirectives(directives);
        d.setExpression(expression);
        d.setMediums(mediums);
        d.setRuleSets(ruleSet);
        return d;
    }

    public ImportDirective buildImportDirective() {
        ImportDirective d = new ImportDirective();
        d.setExpression(expression);
        if (mediums.size() == 0) {
            d.setMedium(Medium.all);
        }
        else {
            d.setMedium(mediums.get(0));
        }
        return d;
    }

    public IncludeDirective buildIncludeDirective() {
        IncludeDirective d = new IncludeDirective();
        d.setExpression(expression);
        return d;
    }

    public Directive buildMediaDirective() {
        GenericDirective d = new GenericDirective();
        d.setType(DirectiveType.Media);
        d.setName("@media");
        d.setMediums(mediums);
        d.setRuleSets(ruleSet);
        return d;
    }

    public NamespaceDirective buildNamespaceDirective() {
        NamespaceDirective d = new NamespaceDirective();
        d.setPrefix(id);
        d.setExpression(expression);
        return d;
    }

    public PageDirective buildPageDirective() {
        PageDirective d = new PageDirective();
        d.setSelector(ss);
        d.setDeclarations(declarations);
        return d;
    }
}
