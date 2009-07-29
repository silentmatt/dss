package com.silentmatt.dss.directive;

import com.silentmatt.dss.DSSEvaluator;
import com.silentmatt.dss.Declaration;
import com.silentmatt.dss.Rule;
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
    public void evaluate(DSSEvaluator.EvaluationState state, List<Rule> container) {
        DSSEvaluator.evaluateStyle(state, getDeclarations(), true);
    }
}
