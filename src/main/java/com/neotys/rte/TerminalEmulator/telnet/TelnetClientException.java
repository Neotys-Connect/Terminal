package com.neotys.rte.TerminalEmulator.telnet;

/**
 * Created by hrexed on 25/07/18.
 */
public final class TelnetClientException extends Exception
{
    private static final long serialVersionUID = -3289594500081605811L;

    public TelnetClientException(final Throwable cause) {
        super(cause);
    }
}