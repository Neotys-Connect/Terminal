package com.neotys.rte.TerminalEmulator;

/**
 * Created by hrexed on 15/06/18.
 */
public class RTETimeOutException extends Exception {

    public RTETimeOutException() {}

    // Constructor that accepts a message
    public RTETimeOutException(String message)
    {
        super(message);
    }
}
