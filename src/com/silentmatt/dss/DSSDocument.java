package com.silentmatt.dss;

import com.google.common.collect.ImmutableList;
import com.silentmatt.dss.error.ErrorReporter;
import com.silentmatt.dss.evaluator.ResourceLocator;
import com.silentmatt.dss.parser.DSSParser;
import com.silentmatt.dss.parser.Scanner;
import com.silentmatt.dss.rule.Rule;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Represents a parsed DSS document.
 *
 * @author Matthew Crumley
 */
public class DSSDocument {
    public static class Builder {
        ImmutableList.Builder<Rule> rules = ImmutableList.builder();

        public Builder addRule(Rule rule) {
            rules.add(rule);
            return this;
        }

        public DSSDocument build() {
            return new DSSDocument(rules.build());
        }
    }

    private final ImmutableList<Rule> rules;

    /**
     * Parses a DSS document from a URL string.
     *
     * @param resourceLocator The {@link ResourceLocator} used to open the URL.
     * @param url The URL of the document to parse.
     * @param errors Where to report errors.
     *
     * @return The parsed DSSDocument.
     *
     * @throws IOException if the URL is invalid or there is an error reading the document.
     */
    public static DSSDocument parse(ResourceLocator resourceLocator, String url, ErrorReporter errors) throws IOException {
        return parse(resourceLocator, new URL(url), errors);
    }

    /**
     * Parses a DSS document from a URL.
     *
     * @param resourceLocator The {@link ResourceLocator} used to open the URL.
     * @param url The URL of the document to parse.
     * @param errors Where to report errors.
     *
     * @return The parsed DSSDocument.
     *
     * @throws IOException if there is an error reading the document.
     */
    public static DSSDocument parse(ResourceLocator resourceLocator, URL url, ErrorReporter errors) throws IOException {
        return parse(resourceLocator.openResource(url), errors);
    }

    /**
     * Parses a DSS document from an {@link InputStream}.
     *
     * @param input The stream to read the document from.
     * @param errors Where to report errors.
     *
     * @return The parsed DSSDocument.
     *
     * @throws IOException if there is an error reading the document.
     */
    public static DSSDocument parse(InputStream input, ErrorReporter errors) throws IOException {
        Scanner scanner = new Scanner(input);
        return parse(scanner, errors);
    }

    /**
     * Parses a DSS document from a {@link Scanner}.
     *
     * @param scanner The {@link Scanner} object to read the document from.
     * @param errors Where to report errors.
     *
     * @return The parsed DSSDocument.
     */
    public static DSSDocument parse(Scanner scanner, ErrorReporter errors) {
        int existingErrors = errors.getErrorCount();
        DSSParser parser = new DSSParser(scanner);
        parser.setErrors(errors);
        parser.parse();
        if (parser.getErrors().getErrorCount() > existingErrors) {
            return null;
        }
        else {
            return parser.getDocument();
        }
    }

    private DSSDocument(ImmutableList<Rule> rules) {
        this.rules = rules;
    }

    /**
     * Gets a list of the top-level rules in the document.
     *
     * @return {@link List} of {@link Rule} objects.
     */
    public ImmutableList<Rule> getRules() {
        return rules;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        for (Rule r : rules) {
            txt.append(r).append("\n");
        }
        return txt.toString();
    }
}
