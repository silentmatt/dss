package com.silentmatt.dss;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.stringparsers.FileStringParser;
import com.martiansoftware.jsap.stringparsers.URLStringParser;
import java.io.File;
import java.io.PrintStream;
import java.net.MalformedURLException;

public class OptionsParser {
    private final JSAP jsap;

    public OptionsParser() {
        jsap = new JSAP();
        setupArguments();
    }

    public Configuration getConfiguration(String[] args) {
        return new JSAPConfiguration(jsap.parse(args));
    }

    public void printUsage(PrintStream out) {
        out.println("usage: java -jar dss.jar");
        out.println("       " + jsap.getUsage());
        out.println();
        out.println(jsap.getHelp());
    }

    private void setupArguments() {
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

        Switch testFlag = new Switch("test")
                .setShortFlag('t')
                .setLongFlag("test");
        testFlag.setHelp("Run tests in the specified directory");

        Switch colorFlag = new Switch("color")
                .setLongFlag("color");
        colorFlag.setHelp("Colorize test output");

        Switch compressFlag = new Switch("compress")
                .setShortFlag('c')
                .setLongFlag("compress");
        compressFlag.setHelp("Compress the CSS output.");

        Switch watchFlag = new Switch("watch")
                .setShortFlag('w')
                .setLongFlag("watch");
        watchFlag.setHelp("Re-process the file any time it changes.");

        Switch notifyFlag = new Switch("notify")
                .setShortFlag('n')
                .setLongFlag("notify");
        notifyFlag.setHelp("Pop-up a notification with notify-send when watched files are finished.");

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
            jsap.registerParameter(testFlag);
            jsap.registerParameter(colorFlag);
            jsap.registerParameter(debugFlag);
            jsap.registerParameter(compressFlag);
            jsap.registerParameter(watchFlag);
            jsap.registerParameter(notifyFlag);
            jsap.registerParameter(defineOpt);
            jsap.registerParameter(outOpt);

            jsap.registerParameter(urlOpt);
        } catch (JSAPException j) {
            System.err.println("Unexpected Error: Illegal JSAP parameter.");
            System.exit(2);
        }
    }

    private static class FileOrURLStringParser extends StringParser {
        private static final FileStringParser fileParser = FileStringParser.getParser();
        private static final URLStringParser urlParser = URLStringParser.getParser();

        private FileOrURLStringParser() {
            fileParser.setMustBeFile(false).setMustExist(true);
        }

        static FileOrURLStringParser getParser() {
            return new FileOrURLStringParser();
        }

        @Override
        public Object parse(String arg) throws ParseException {
            try {
                return urlParser.parse(arg);
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
}
