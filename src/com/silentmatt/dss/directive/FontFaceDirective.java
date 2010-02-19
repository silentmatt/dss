package com.silentmatt.dss.directive;

import com.silentmatt.dss.Declaration;
import com.silentmatt.dss.EvaluationState;
import com.silentmatt.dss.Rule;
import com.silentmatt.dss.css.CssFontFaceDirective;
import com.silentmatt.dss.css.CssRule;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Matthew Crumley
 */
public class FontFaceDirective extends DeclarationDirective {
    public FontFaceDirective(List<Declaration> declarations) {
        super(declarations);
    }

    public String getName() {
        return "@font-face";
    }

    @Override
    public CssRule evaluate(EvaluationState state, List<Rule> container) throws IOException {
        return new CssFontFaceDirective(getDeclarations().evaluateStyle(state, true));
    }
}
