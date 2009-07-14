package com.silentmatt.dss;

/**
 *
 * @author matt
 */
public class ImportDirective extends ExpressionDirective {
    private Medium medium;

    public ImportDirective(Expression url, Medium medium) {
        super(url);
        setMedium(medium);
    }

    public String getName() {
        return "@import";
    }

    public Medium getMedium() {
        return medium;
    }

    public void setMedium(Medium medium) {
        this.medium = medium;
    }

    public DirectiveType getType() {
        return DirectiveType.Import;
    }

    @Override
    public String toString() {
        return getName() + " " + getExpression() + " " + medium + ";";
    }

    @Override
    public String toCompactString() {
        return getName() + " " + getExpression().toCompactString() + medium + ";";
    }

}
