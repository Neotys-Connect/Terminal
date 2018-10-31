package com.neotys.rte.TerminalEmulator.telnet;

import java.lang.reflect.Executable;

public class TelnetSessionException extends Exception {
    private static final long serialVersionUID = -3289594500081605811L;

    public TelnetSessionException(final Throwable cause) {
        super(cause);
    }

    public TelnetSessionException(String text) {
        super(text);
    }
}
