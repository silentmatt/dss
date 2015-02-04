package com.silentmatt.dss;

import com.silentmatt.dss.error.ErrorReporter;
import com.silentmatt.dss.error.ExceptionErrorReporter;
import com.silentmatt.dss.error.NullErrorReporter;
import com.silentmatt.dss.evaluator.DSSEvaluator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public class TestRunner {
    public int runTests(URL directory, boolean color) {
        File dir = getTestDirectory(directory);
        if (dir == null) {
            System.err.println("Invalid test directory.");
            return 1;
        }

        int errors = 0;
        String[] dirList = dir.list(new DssFilenameFilter());
        Arrays.sort(dirList);
        for (String dssFileName : dirList) {
            try {
                System.out.print(dssFileName.replace(".dss", "") + ": ");
                String result = testDssFile(new URL(directory, dssFileName));
                showResult(result, color);
                if (!result.equals("PASS")) {
                    ++errors;
                }
            } catch (MalformedURLException ex) {
                System.err.println("Invalid DSS file: " + dssFileName);
            }
        }

        if (errors == 0) {
            if (color) {
                System.out.println("\033[32mAll tests passed.\033[0m");
            }
            else {
                System.out.println("All tests passed.");
            }
        }
        else {
            if (color) {
                System.out.println("\033[31m" + errors + " test" + (errors != 1 ? "s" : "") + " failed.\033[0m");
            }
            else {
                System.out.println(errors + " test" + (errors != 1 ? "s" : "") + " failed.");
            }
        }
        return errors;
    }

    private static void showResult(String result, boolean color) {
        if (!color) {
            System.out.println(result);
        }
        else if (result.equals("PASS")) {
            System.out.println("\033[32mPASS\033[0m");
        }
        else {
            System.out.println("\033[31m" + result + "\033[0m");
        }
    }

    private static String testDssFile(URL url) {
        File cssFile;
        File dssFile;
        File minFile;
        try {
            dssFile = new File(url.toURI());
            cssFile = new File(new File(url.toURI()).getAbsolutePath().replace(".dss", ".css"));
            minFile = new File(new File(url.toURI()).getAbsolutePath().replace(".dss", ".min.css"));
            if (!cssFile.exists()) {
                // HACK: This is an ugly way to do this...
                throw new URISyntaxException("", "");
            }
        } catch (URISyntaxException ex) {
            return "Could not find css file";
        }

        String min = minFile.exists() ? readFile(minFile) : null;
        switch (testString(url, readFile(dssFile), readFile(cssFile), min)) {
        case 0:
            return "PASS";
        case 1:
            return "FAIL";
        case 2:
            return "FAIL (compressed)";
        default:
            return "FAIL";
        }
    }

    private static int testString(URL url, String dssString, String correct, String minified) {
        String normalCSS = compile(url, dssString, false);
        if (normalCSS == null) {
            return 1;
        }
        String compressed = compile(url, dssString, true);
        if (compressed == null) {
            return 2;
        }

        // Workaround for tests that use "@include literal". We should really be using an actual test framework.
        String decompressed = correct;
        if (!url.toString().endsWith("-literal.dss")) {
            decompressed = compile(url, compressed, false);
        }
        if (decompressed == null) {
            return 2;
        }

        if (!normalCSS.equals(correct)) {
            return 1;
        }
        if ((minified == null && !decompressed.equals(correct)) || (minified != null && !compressed.equals(minified))) {
            return 2;
        }

        return 0;
    }

    private static String compile(URL url, String dssString, boolean compact) {
        try {
            ErrorReporter errors = new ExceptionErrorReporter(new NullErrorReporter());
            DSSDocument dss = DSSDocument.parse(new ByteArrayInputStream(dssString.getBytes()), errors);
            DSSEvaluator.Options opts = new DSSEvaluator.Options(url);
            opts.setErrors(errors);
            return new DSSEvaluator(opts).evaluate(dss).toString(compact);
        }
        catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private static String readFile(File cssFile) {
        try {
            RandomAccessFile raf = new RandomAccessFile(cssFile, "r");
            byte[] contents = new byte[(int)raf.length()];
            raf.readFully(contents);
            return new String(contents);
        }
        catch (IOException ex) {
            return null;
        }
    }

    private File getTestDirectory(URL directory) {
        if (directory == null) {
            try {
                directory = new URL(".");
            } catch (MalformedURLException ex) {
                System.err.println("Fatal error in runTests.");
                return null;
            }
        }

        File dir;
        try {
            dir = new File(directory.toURI());
        } catch (URISyntaxException ex) {
            dir = null;
        }

        if (dir != null && !dir.isDirectory()) {
            dir = null;
        }

        return dir;
    }

    private static class DssFilenameFilter implements FilenameFilter {
        @Override
        public boolean accept(File directory, String filename) {
            return filename.toLowerCase().endsWith(".dss");
        }
    }
}
