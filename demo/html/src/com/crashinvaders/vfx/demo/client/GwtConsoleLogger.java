package com.crashinvaders.vfx.demo.client;

import com.badlogic.gdx.ApplicationLogger;

public class GwtConsoleLogger implements ApplicationLogger {

    @Override
    public void log (String tag, String message) {
        consoleLog(tag + ": " + message);
    }

    @Override
    public void log (String tag, String message, Throwable exception) {
        consoleLog(tag + ": " + message + "\n" + getMessages(exception));
    }

    @Override
    public void error (String tag, String message) {
        consoleError(tag + ": " + message);
    }

    @Override
    public void error (String tag, String message, Throwable exception) {
        consoleError(tag + ": " + message + "\n" + getMessages(exception));
    }

    @Override
    public void debug (String tag, String message) {
        consoleLog(tag + ": " + message);
    }

    @Override
    public void debug (String tag, String message, Throwable exception) {
        consoleLog(tag + ": " + message + "\n" + getMessages(exception));
    }

    private String getMessages (Throwable e) {
        StringBuffer buffer = new StringBuffer();
        while (e != null) {
            buffer.append(e.getMessage() + "\n");
            e = e.getCause();
        }
        return buffer.toString();
    }

    private String getStackTrace (Throwable e) {
        StringBuffer buffer = new StringBuffer();
        for (StackTraceElement trace : e.getStackTrace()) {
            buffer.append(trace.toString() + "\n");
        }
        return buffer.toString();
    }

    private native void consoleLog(String message) /*-{
        console.log(message);
    }-*/;

    private native void consoleError(String message) /*-{
        console.error(message);
    }-*/;
}

