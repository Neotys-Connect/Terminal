package com.neotys.rte.TerminalEmulator.telnet;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;

/**
 * Created by hrexed on 25/07/18.
 */
final class TelnetConnector {
    static final TelnetConnector INSTANCE = new TelnetConnector();

    protected TelnetClient createSession(final String host, final int port, final String username, final String password,String TerminalType,int timeout) throws TelnetClientException
    {
        try {

            TelnetClient telnetClient=new TelnetClient(TerminalType);

            telnetClient.setDefaultTimeout(timeout);

            telnetClient.connect(host,port);
            return telnetClient;
        } catch (final IOException e) {
            throw new TelnetClientException(e);
        }
    }


}
