package com.silentmatt.dss.evaluator;

import java.net.URL;

/**
 * A callback that responds to URL events.
 * Specifically, this gets called when a file is included by DSS.
 * 
 * @author Matthew Crumley
 */
public interface URLCallback {
    /**
     * Handles the event.
     *
     * @param url The {@link URL} that the event is related to.
     */
    void call(URL url);
}
