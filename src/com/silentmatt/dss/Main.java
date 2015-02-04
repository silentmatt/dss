package com.silentmatt.dss;

import com.silentmatt.dss.error.ErrorReporter;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public final class Main {

    private Main() {
    }

    private static void printVersion() {
        System.out.println("DSS 0.2");
    }

    public static void main(String[] args) {
        OptionsParser optionsParser = new OptionsParser();
        Configuration config = optionsParser.getConfiguration(args);

        if (config.showVersion()) {
            printVersion();
            System.exit(0);
        }

        if (!config.isSuccess()) {
            optionsParser.printUsage(System.err);
            System.exit(1);
        }

        if (config.isTest()) {
            System.exit(new TestRunner().runTests(config.getURL(), config.colorTestOutput()));
        }

        URL url = config.getURL();
        File out = config.getOutputFile();

        if (out != null) {
            try {
                if (new File(url.toURI()).isDirectory()) {
                    throw new MalformedURLException();
                }
                if (url.sameFile(out.toURI().toURL())) {
                    System.err.println("Input and output are the same file.");
                    System.exit(1);
                }
            } catch (MalformedURLException | URISyntaxException ex) {
                System.err.println("Invalid file: " + out.getPath());
                System.exit(1);
            }
        }

        if (url != null) {
            ErrorReporter errors;

            if (config.watchFile()) {
                errors = new FileProcessor(config).watchFile(url, out);
            }
            else {
                errors = new FileProcessor(config).processFile(url, out);
            }

            if (errors.getErrorCount() > 0) {
                System.exit(1);
            }
        }
        else {
            System.err.println("Missing url parameter.");
            optionsParser.printUsage(System.err);
            System.exit(1);
        }
    }
}
