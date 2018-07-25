package com.neotys.rte.TerminalEmulator.telnet;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteSource;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteStream;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteStreamListener;
import org.apache.commons.net.telnet.TelnetInputListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hrexed on 25/07/18.
 */
public class DefaultTelnetStream implements RteStream {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("RteStream-%d").build());

    private final TelnetSource source;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private final Lock bufferLock = new ReentrantLock();
  //  private final List<TelnetSessionListener> listeners = new ArrayList<TelnetSessionListener>();

    public DefaultTelnetStream(TelnetSource source) {
        super();
        this.source = source;
        this.source.GetClient().setReaderThread(true);
        this.source.GetClient().registerInputListener(new TelnetInputListener() {
            @Override
            public void telnetInputAvailable() {
                /*try {
                    readInput();
                } catch (IOException e) {
                    notifyInputError(e);
                }*/
            }
        });
    }
    /*
    private void notifyInputError(IOException exception) {
        for (TelnetSessionListener listener : this.listeners) {
            listener.error(exception);
        }
    }

    private void readInput() throws IOException {
        synchronized (this.charBuffer) {
            this.reader.read(this.charBuffer);
            this.charBuffer.notifyAll();

            if (this.charBuffer.position() > 0) {
                notifyInputAvailable();
            }
        }
    }
*/
    /*static RteStream of(final RteSource source) {
        return new DefaultTelnetStream(source);
    }*/


    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public byte[] bufferClear() {
        return new byte[0];
    }

    @Override
    public boolean bufferContains(byte[]... masks) {
        return false;
    }

    @Override
    public byte[] bufferCopy() {
        return new byte[0];
    }

    @Override
    public void addListener(RteStreamListener listener) {

    }

    @Override
    public void removeListener(RteStreamListener listener) {

    }

    @Override
    public void close() throws IOException {

    }

}
