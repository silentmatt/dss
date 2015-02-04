package com.silentmatt.dss.evaluator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Gets input streams for resources given a URL.
 *
 * @author Matthew Crumley
 */
public interface ResourceLocator {
    InputStream openResource(URL url) throws IOException;
}
