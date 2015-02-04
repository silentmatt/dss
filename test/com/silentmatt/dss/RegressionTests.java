package com.silentmatt.dss;

import com.google.common.base.Charsets;
import com.silentmatt.dss.error.ErrorReporter;
import com.silentmatt.dss.error.ExceptionErrorReporter;
import com.silentmatt.dss.error.NullErrorReporter;
import com.silentmatt.dss.evaluator.DSSEvaluator;
import com.silentmatt.dss.evaluator.DefaultResourcesLocator;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

public class RegressionTests {
    static Set<String> testedFiles;

    @BeforeClass
    public static void setUpClass() {
        testedFiles = new HashSet<>();
    }

    @AfterClass
    public static void tearDownClass() {
        assertEquals(43, testedFiles.size());
    }

    private void testString(URL url, String dssString, String correct, String minified) {
        String normalCSS = compile(url, dssString, false);
        assertNotNull("Normout output failed", normalCSS);

        String compressed = compile(url, dssString, true);
        assertNotNull("Compressed output failed", compressed);

        // Workaround for tests that use "@include literal". We should really be using an actual test framework.
        String decompressed = correct;
        if (!url.toString().endsWith("-literal.dss")) {
            decompressed = compile(url, compressed, false);
        }
        assertNotNull("Compiling compressed output failed", decompressed);

        assertEquals("Incorrect output", correct, normalCSS);

        if (minified == null) {
            assertEquals("Incorrect output", correct, decompressed);
        }
        if (minified != null) {
            assertEquals("Incorrect minified output", minified, compressed);
        }
    }

    private String compile(URL url, String dssString, boolean compact) {
        try {
            ErrorReporter errors = new ExceptionErrorReporter(new NullErrorReporter());
            DSSDocument dss = DSSDocument.parse(new ByteArrayInputStream(dssString.getBytes(Charsets.UTF_8)), errors);
            DSSEvaluator.Options opts = new DSSEvaluator.Options(url);
            opts.setErrors(errors);
            opts.setResourceLocator(new DefaultResourcesLocator());
            return new DSSEvaluator(opts).evaluate(dss).toString(compact);
        }
        catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private void testDssFile(String filename) {
        assertFalse("File tested twice", testedFiles.contains(filename));
        testedFiles.add(filename);

        try {
            URL url = new URL(new File("./test").toURI().toURL(), filename);
            File dssFile = new File(url.toURI());
            File cssFile = new File(new File(url.toURI()).getAbsolutePath().replace(".dss", ".css"));
            File minFile = new File(new File(url.toURI()).getAbsolutePath().replace(".dss", ".min.css"));
            if (!cssFile.exists()) {
                fail("Could not find css file " + cssFile);
                return;
            }
            String min = minFile.exists() ? readFile(minFile) : null;
            testString(url, readFile(dssFile), readFile(cssFile), min);
        } catch (URISyntaxException | MalformedURLException ex) {
            fail("Could not find css file");
        }
    }

    private String readFile(File cssFile) {
        try {
            RandomAccessFile raf = new RandomAccessFile(cssFile, "r");
            byte[] contents = new byte[(int)raf.length()];
            raf.readFully(contents);
            return new String(contents, Charsets.UTF_8);
        }
        catch (IOException ex) {
            return null;
        }
    }

    @Test
    public void animation() {
        testDssFile("animation.dss");
    }

    @Test
    public void atVariables() {
        testDssFile("at-variables.dss");
    }

    @Test
    public void big() {
        testDssFile("big.dss");
    }

    @Test
    public void calc() {
        testDssFile("calc.dss");
    }

    @Test
    public void calcProp() {
        testDssFile("calc-prop.dss");
    }

    @Test
    public void centered() {
        testDssFile("centered.dss");
    }

    @Test
    public void charset() {
        testDssFile("charset.dss");
    }

    @Test
    public void classNested() {
        testDssFile("class-nested.dss");
    }

    @Test
    public void color() {
        testDssFile("color.dss");
    }

    @Test
    public void comments() {
        testDssFile("comments.dss");
    }

    @Test
    public void complexSimpleSelector() {
        testDssFile("complex-simple-selector.dss");
    }

    @Test
    public void css() {
        testDssFile("css.dss");
    }

    @Test
    public void css3() {
        testDssFile("css-3.dss");
    }

    @Test
    public void dashPrefix() {
        testDssFile("dash-prefix.dss");
    }

    @Test
    public void evalInsideFunctions() {
        testDssFile("eval-inside-functions.dss");
    }

    @Test
    public void identity() {
        testDssFile("identity.dss");
    }

    @Test
    public void ifTest() {
        testDssFile("if.dss");
    }

    @Test
    public void ifDefine() {
        testDssFile("if-define.dss");
    }

    @Test
    public void importTest() {
        testDssFile("import.dss");
    }

    @Test
    public void include() {
        testDssFile("include.dss");
    }

    @Test
    public void includeLiteral() {
        testDssFile("include-literal.dss");
    }

    @Test
    public void includeRecursive() {
        testDssFile("include-recursive.dss");
    }

    @Test
    public void keyframes() {
        testDssFile("keyframes.dss");
    }

    @Test
    public void lazyEval() {
        testDssFile("lazy-eval.dss");
    }

    @Test
    public void media() {
        testDssFile("media.dss");
    }

    @Test
    public void mixinsArgs() {
        testDssFile("mixins-args.dss");
    }

    @Test
    public void mixinsAtArgs() {
        testDssFile("mixins-at-args.dss");
    }

    @Test
    public void multipleDeclarations() {
        testDssFile("multiple-declarations.dss");
    }

    @Test
    public void namespace() {
        testDssFile("namespace.dss");
    }

    @Test
    public void nested() {
        testDssFile("nested.dss");
    }

    @Test
    public void nestedClass() {
        testDssFile("nested-class.dss");
    }

    @Test
    public void nestedIfDefine() {
        testDssFile("nested-if-define.dss");
    }

    @Test
    public void nestedParam() {
        testDssFile("nested-param.dss");
    }

    @Test
    public void page() {
        testDssFile("page.dss");
    }

    @Test
    public void parens() {
        testDssFile("parens.dss");
    }

    @Test
    public void rulesetReference() {
        testDssFile("ruleset-reference.dss");
    }

    @Test
    public void rulesets() {
        testDssFile("rulesets.dss");
    }

    @Test
    public void scope() {
        testDssFile("scope.dss");
    }

    @Test
    public void selectors() {
        testDssFile("selectors.dss");
    }

    @Test
    public void strings() {
        testDssFile("strings.dss");
    }

    @Test
    public void termSeparator() {
        testDssFile("term-separator.dss");
    }

    @Test
    public void variables() {
        testDssFile("variables.dss");
    }

    @Test
    public void whitespace() {
        testDssFile("whitespace.dss");
    }
}
