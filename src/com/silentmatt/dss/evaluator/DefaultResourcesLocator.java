package com.silentmatt.dss.evaluator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * The default implementation of {@link ResourceLocator} that uses URL#openStream()
 *
 * @author Matthew Crumley
 */
public class DefaultResourcesLocator implements ResourceLocator {

    @Override
    public InputStream openResource(URL url) throws IOException {
        return url.openStream();
    }
}
