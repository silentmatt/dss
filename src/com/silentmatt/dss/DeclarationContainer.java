package com.silentmatt.dss;

import java.util.List;

/**
 *
 * @author matt
 */
public interface DeclarationContainer {
    List<Declaration> getDeclarations();
    void setDeclarations(List<Declaration> declarations);
    void addDeclaration(Declaration declaration);

    Declaration getDeclaration(String name);
    Expression getValue(String name);

    String toCompactString();

    @Override
    String toString();

    String toString(int nesting, boolean compact);
}
