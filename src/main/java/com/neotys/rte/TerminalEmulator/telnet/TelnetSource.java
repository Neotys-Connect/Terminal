package com.neotys.rte.TerminalEmulator.telnet;

import com.jcraft.jsch.Channel;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteSource;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetInputListener;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hrexed on 25/07/18.
 */
final class TelnetSource implements RteSource {
    private final InputStream inputChannel;
    private final TelnetClient channel;
    private final byte[] buffer = new byte[1024];

    private TelnetSource(final TelnetClient channel) throws IOException {
        super();

        this.inputChannel = channel.getInputStream();
        this.channel = channel;
    }

    public static RteSource of(final TelnetClient channel) throws IOException {
        return new TelnetSource(channel);
    }

   public TelnetClient getClient()
   {
       return channel;
   }
    @Override
    public boolean isAlive() {
        return channel.isConnected() && channel.isAvailable() ;
    }

    @Override
    public byte[] read() throws IOException {
        final int read = inputChannel.read(buffer, 0, buffer.length);
        if(read==-1)
            return null;

        final byte[] result = new byte[read];
        System.arraycopy(buffer, 0, result, 0, read);
        return result;
    }
}