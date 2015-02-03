package com.silentmatt.dss.error;

/**
 * A warning message.
 *
 * @author Matthew Crumley
 */
public class WarningMessage extends Message {
    public WarningMessage(Position position, String message) {
        super(position, message);
    }

    public WarningMessage(String message) {
        this(null, message);
    }

    @Override
    protected String getType() {
        return "Warning";
    }
}
