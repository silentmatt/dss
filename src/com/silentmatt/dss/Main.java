package com.silentmatt.dss;

import com.martiansoftware.jsap.*;
import com.martiansoftware.jsap.stringparsers.FileStringParser;
import com.silentmatt.dss.css.CssDocument;
import com.silentmatt.dss.parser.DSSParser;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.*;

public final class Main {
    private Main() {
    }

    private static void printUsage(JSAP jsap) {
        System.err.println("usage: java -jar dss.jar");
        System.err.println("       " + jsap.getUsage());
        System.err.println();
        System.err.println(jsap.getHelp());
    }

    private static void printVersion() {
        System.out.println("DSS 0.1b");
    }

    @SuppressWarnings("deprecation")
    private static class FileOrURLStringParser extends com.martiansoftware.jsap.stringparsers.URLStringParser {
        private static FileStringParser fileParser = FileStringParser.getParser();

        private FileOrURLStringParser() {
            fileParser.setMustBeFile(true).setMustExist(true);
        }

        public static FileOrURLStringParser getParser() {
            return new FileOrURLStringParser();
        }

        @Override
        public Object parse(String arg) throws ParseException {
            try {
                return super.parse(arg);
            } catch (ParseException ex) {
                fileParser.setUp();
                File file = (File) fileParser.parse(arg);
                fileParser.tearDown();
                try {
                    return file.toURI().toURL();
                } catch (MalformedURLException ex1) {
                    throw new ParseException(ex1);
                }
            }
        }
    }

    private static void setupArguments(JSAP jsap) {
        FlaggedOption outOpt = new FlaggedOption("out")
                .setStringParser(FileStringParser.getParser().setMustBeFile(true).setMustExist(false))
                .setRequired(false)
                .setAllowMultipleDeclarations(false)
                .setShortFlag('o');
        outOpt.setHelp("File to save outout to");

        Switch debugFlag = new Switch("debug")
                .setLongFlag("debug");
        debugFlag.setHelp("Don't remove DSS directives from output");

        Switch versionFlag = new Switch("version")
                .setShortFlag('v')
                .setLongFlag("version");
        versionFlag.setHelp("Show version information and exit");

        FlaggedOption defineOpt = new FlaggedOption("define")
                .setAllowMultipleDeclarations(true)
                .setRequired(false)
                .setShortFlag('d')
                .setLongFlag("define")
                .setUsageName("name:value")
                .setStringParser(JSAP.STRING_PARSER);
        defineOpt.setHelp("Pre-define a constant in the global namespace");

        UnflaggedOption urlOpt = new UnflaggedOption("url")
                .setStringParser(FileOrURLStringParser.getParser())
                .setRequired(true);
        urlOpt.setHelp("The filename or URL of the DSS file.");

        try {
            jsap.registerParameter(versionFlag);
            jsap.registerParameter(debugFlag);
            jsap.registerParameter(defineOpt);
            jsap.registerParameter(outOpt);

            jsap.registerParameter(urlOpt);
        } catch (JSAPException j) {
            System.err.println("Unexpected Error: Illegal JSAP parameter.");
            System.exit(2);
        }
    }

    public static void main(String[] args) {
        JSAP jsap = new JSAP();
        setupArguments(jsap);

        JSAPResult config = jsap.parse(args);
        if (config.getBoolean("version")) {
            printVersion();
            System.exit(0);
        }

        if (!config.success()) {
            printUsage(jsap);
            System.exit(1);
        }

        URL url = config.getURL("url");
        File out = config.getFile("out", null);

        if (out != null) {
            try {
                if (url.sameFile(out.toURI().toURL())) {
                    System.err.println("Input and output are the same file.");
                    System.exit(1);
                }
            } catch (MalformedURLException ex) {
                System.err.println("Invalid file: " + out.getPath());
                System.exit(1);
            }
        }

        DSSEvaluator.Options opts = new DSSEvaluator.Options(url);
        ErrorReporter errors = new PrintStreamErrorReporter();
        opts.setErrors(errors);

        String[] defines = config.getStringArray("define");
        for (String define : defines) {
            Declaration declaration = DSSParser.parseDeclaration(define, errors);
            opts.getVariables().declare(declaration.getName(), declaration.getExpression());
        }

        if (url != null) {
            try {
                DSSDocument css = DSSDocument.parse(url, errors);
                if (css != null) {

                    CssDocument outputDocument = new DSSEvaluator(opts).evaluate(css);

                    String cssString;
                    if (config.getBoolean("debug")) {
                        cssString = css.toString();
                    }
                    else {
                        //cssString = css.toCssString();
                        cssString = outputDocument.toString();
                    }

                    if (out == null) {
                        System.out.println(cssString);
                    }
                    else {
                        PrintStream pout = new PrintStream(out);
                        pout.println(cssString);
                        pout.close();
                    }
                }
            } catch (MalformedURLException ex) {
                errors.SemErr("DSS: Invalid URL");
            } catch (IOException ex) {
                errors.SemErr("DSS: I/O error: " + ex.getMessage());
            }
        }
        else {
            System.err.println("Missing url parameter.");
            printUsage(jsap);
            System.exit(1);
        }

        if (errors.getErrorCount() > 0) {
            System.exit(1);
        }
    }
}
