package com.silentmatt.dss;

import com.martiansoftware.jsap.JSAPResult;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class JSAPConfiguration implements Configuration {
    private final JSAPResult config;

    public JSAPConfiguration(JSAPResult config) {
        this.config = config;
    }

    @Override
    public boolean isSuccess() {
        return config.success();
    }

    @Override
    public boolean showVersion() {
        return config.getBoolean("version");
    }

    @Override
    public boolean isTest() {
        return config.getBoolean("test");
    }

    @Override
    public URL getUrl() {
        return config.getURL("url");
    }

    @Override
    public boolean colorTestOutput() {
        return config.getBoolean("color");
    }

    @Override
    public File getOutputFile() {
        return config.getFile("out", null);
    }

    @Override
    public List<String> getDefines() {
        return Arrays.asList(config.getStringArray("define"));
    }

    @Override
    public boolean watchFile() {
        return config.getBoolean("watch");
    }

    @Override
    public boolean showDebuggingOutput() {
        return config.getBoolean("debug");
    }

    @Override
    public boolean compressOutput() {
        return config.getBoolean("compress");
    }

    @Override
    public boolean showNotifications() {
        return config.getBoolean("notify");
    }
}
