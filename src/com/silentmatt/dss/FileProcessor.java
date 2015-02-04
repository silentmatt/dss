package com.silentmatt.dss;

import com.silentmatt.dss.css.CssDocument;
import com.silentmatt.dss.declaration.Declaration;
import com.silentmatt.dss.error.ErrorReporter;
import com.silentmatt.dss.error.PrintStreamErrorReporter;
import com.silentmatt.dss.evaluator.DSSEvaluator;
import com.silentmatt.dss.evaluator.DefaultResourcesLocator;
import com.silentmatt.dss.evaluator.URLCallback;
import com.silentmatt.dss.parser.DSSParser;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class FileProcessor {
    private final Configuration config;

    public FileProcessor(Configuration config) {
        this.config = config;
    }

    private DSSEvaluator.Options getOptions(URL url, ErrorReporter errors) {
        DSSEvaluator.Options opts = new DSSEvaluator.Options(url);
        opts.setErrors(errors);

        List<String> defines = config.getDefines();
        for (String define : defines) {
            Declaration declaration = DSSParser.parseDeclaration(define, errors);
            opts.getVariables().declare(declaration.getName(), declaration.getExpression());
        }

        return opts;
    }

    public ErrorReporter processFile(URL url, File out) {
        ErrorReporter errors = new PrintStreamErrorReporter();
        DSSEvaluator.Options opts = getOptions(url, errors);

        try {
            DSSDocument css = DSSDocument.parse(new DefaultResourcesLocator(), url, errors);
            if (css != null) {
                CssDocument outputDocument = new DSSEvaluator(opts).evaluate(css);
                String cssString;
                if (config.showDebuggingOutput()) {
                    cssString = css.toString();
                } else {
                    cssString = outputDocument.toString(config.compressOutput());
                }
                if (out == null) {
                    System.out.print(cssString);
                } else {
                    try (PrintStream pout = new PrintStream(out, "UTF-8")) {
                        pout.print(cssString);
                    }
                }
            }
        } catch (MalformedURLException ex) {
            errors.semanticError("DSS: Invalid URL");
        } catch (IOException ex) {
            errors.semanticError("DSS: I/O error: " + ex.getMessage());
        }

        return errors;
    }

    public ErrorReporter watchFile(URL url, File out) {
        ErrorReporter errors = new PrintStreamErrorReporter();

        if (out == null) {
            errors.semanticError("watch requires an output file");
            return errors;
        }

        DSSEvaluator.Options opts = getOptions(url, errors);

        File dssFile;
        try {
            dssFile = new File(url.toURI());
        } catch (URISyntaxException ex) {
            errors.semanticError("Error converting a URL to a URI.");
            return errors;
        }

        final FileWatcher watcher = new FileWatcher(Arrays.asList(dssFile, out));
        System.out.println("Watching file: " + dssFile);

        opts.setIncludeCallback(new URLCallback() {
            @Override
            public void call(URL url) {
                try {
                    if (url.toURI().getScheme().equalsIgnoreCase("file")) {
                        File f = new File(url.toURI());
                        if (watcher.addFile(f)) {
                            System.out.println("    Watching file: " + f);
                        }
                    }
                } catch (URISyntaxException ex) {
                    // Nothing we can do
                }
            }
        });

        System.out.println("Compiling.");
        processFile(url, out);
        watcher.ignoreChanges(out);
        System.out.println("Done Compiling.");

        while (true) {
            if (watcher.filesChanged()) {
                System.out.println(new Date().toString() + " -- File changed. Recompiling.");
                int oldErrorCount = errors.getErrorCount();
                processFile(url, out);
                int errorCount = errors.getErrorCount();
                watcher.ignoreChanges(out);
                System.out.println("Done Compiling.");

                if (config.showNotifications()) {
                    String message;
                    if (errorCount != oldErrorCount) {
                        message = "Error compiling " + dssFile + ".";
                    }
                    else {
                        message = "Done compiling " + dssFile + ".";
                    }
                    try {
                        Runtime.getRuntime().exec(new String[]{ "notify-send", "-t", "2000", message });
                    } catch (IOException ex) {
                        // Do nothing
                    }
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.exit(0);
            }
        }
    }
}
