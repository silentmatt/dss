package com.silentmatt.dss;

/**
 * A syntax error message.
 *
 * @author Matthew Crumley
 */
public class SyntaxErrorMessage extends Message {
    public SyntaxErrorMessage(Position position, String message) {
        super(position, message);
    }

    @Override
    protected String getType() {
        return "Syntax Error";
    }
}
