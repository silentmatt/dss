package com.silentmatt.dss;

import java.io.File;
import java.net.URL;
import java.util.List;

public interface Configuration {
    boolean colorTestOutput();

    boolean compressOutput();

    List<String> getDefines();

    File getOutputFile();

    URL getUrl();

    boolean isSuccess();

    boolean isTest();

    boolean showDebuggingOutput();

    boolean showNotifications();

    boolean showVersion();

    boolean watchFile();
}
