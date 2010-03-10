package com.silentmatt.dss;

/**
 * An error message.
 *
 * @author Matthew Crumley
 */
public class ErrorMessage extends Message {
    public ErrorMessage(Position position, String message) {
        super(position, message);
    }

    public ErrorMessage(String message) {
        this(null, message);
    }

    @Override
    protected String getType() {
        return "Error";
    }
}
