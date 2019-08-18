/*******************************************************************************
 * Copyright 2019 metaphore
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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

