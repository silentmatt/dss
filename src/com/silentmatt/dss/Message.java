package com.silentmatt.dss;

/**
 * A message to be reported from the parser or evaluator.
 *
 * @author Matthew Crumley
 */
public abstract class Message {
    public static class Position {
        private final int line, column;

        public Position(int line, int col) {
            this.line = line;
            this.column = col;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }
    }

    private final Position position;
    private final String message;

    protected Message(Position position, String message) {
        this.position = position;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    protected abstract String getType();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getType()).append(": ");

        if (position != null) {
            sb.append("line ").append(position.getLine());
            sb.append(" col ").append(position.getColumn());
            sb.append(": ");
        }

        sb.append(getMessage());

        return sb.toString();
    }
}
