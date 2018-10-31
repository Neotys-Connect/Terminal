package com.neotys.rte.TerminalEmulator.telnet;

import com.google.common.primitives.Bytes;
import com.neotys.extensions.action.engine.Context;
import com.neotys.rte.TerminalEmulator.RTETimeOutException;
import com.neotys.rte.TerminalEmulator.ch.SpecialKeys;
import com.neotys.rte.TerminalEmulator.ch.SpecialKeysConverter;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteKeys;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteStream;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteStreamListener;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class TelnetChannel {
    private final TelnetClient channel;
    private final RteStream rteStream;
    private final OutputStream outputStream;
    private final Context context;

    private static final byte[] SCREEN_NAME = new byte[] {Ascii.ESC, '[', '1', ';', '1','5', 'H'};

    private TelnetChannel(final TelnetClient channel, Context context) throws IOException {
        this.channel = channel;
        this.rteStream = RteStream.of(TelnetSource.of(channel));
        this.outputStream = channel.getOutputStream();
        this.context=context;


    }

    protected static TelnetChannel of(final TelnetClient session, Context context,boolean enablePtty) throws TelnetSessionException {
        try {
            return new TelnetChannel(TelnetConnector.INSTANCE.createSession(session,enablePtty),context);
        } catch (IOException e) {
            throw new TelnetSessionException(e);
        }
    }

    public boolean isConnected() {
        return channel.isConnected();
    }

    public void close() {
        try {
            rteStream.close();
        } catch (IOException e) {
        }
        try {
            channel.disconnect();
        } catch (IOException e) {
        }
    }


    public String sendKeys(final String text, final int timeout,final boolean NoWaitforEcho,boolean clearbuffer) throws TelnetSessionException, RTETimeOutException {
        if (!channel.isConnected()) throw new TelnetSessionException(new RuntimeException("Channel is already close"));


        //-----clear the buffer if requested----
        if(clearbuffer)
            this.rteStream.bufferClear();

        if(!NoWaitforEcho)
            sendTextKeysWithWaitingEcho(text, timeout);
        else
            sendTextKeysWithoutWaitingEcho(text.getBytes());
        return "";
    }
    public String getBufferContent(boolean cleanoutput)
    {
        String content;
        if(cleanoutput)
            content=bytesToString(this.rteStream.bufferCopy());
        else
            content=new String(this.rteStream.bufferCopy());

        this.rteStream.bufferClear();
        return content;
    }

    public String sendKeysAndWaitFor(final String text, final HashMap<Integer,String> pattern, final String Operator, final int timeout, boolean clearbuffer) throws TelnetSessionException, RTETimeOutException {
        if (!channel.isConnected()) throw new TelnetSessionException(new RuntimeException("Channel is already close"));

        final CountDownLatch l = new CountDownLatch(1);
        final AtomicReference<String> result = new AtomicReference<String>("");

        final RteStreamListener listener  = startListingPattern(result,pattern,Operator, l);

        //-----clear the buffer if requested----
        if(clearbuffer)
            this.rteStream.bufferClear();

        sendTextKeysWithoutWaitingEcho(text.getBytes());

        return waitForListeningPattern(listener,timeout, l, result);
    }

    private boolean CheckPatern(final byte[] buffer,final HashMap<Integer,String> pattern,String Operator)
    {
        int result;

        if(pattern.size()>1)
        {
            for (int key : pattern.keySet())
            {
                result = Bytes.indexOf(buffer, pattern.get(key).getBytes());
                switch (Operator)
                {
                    case "AND":
                        if (result == -1)
                            return false;
                        break;
                    case "OR":
                        if (result != -1)
                            return true;
                        break;
                }
            }
            switch (Operator)
            {
                case "AND":
                    return true;

                case "OR":
                    return false;

            }
        }
        else
        {
            if(Bytes.indexOf(buffer, pattern.get(1).getBytes())!=-1)
                return true;
            else
                return false;
        }
        return false;
    }
    private boolean CheckStringPatern(final byte[] buffer,final HashMap<Integer,String> pattern,String Operator)
    {
        boolean result;
        String content = new String (buffer);

        if(pattern.size()>1)
        {
            for (int key : pattern.keySet())
            {
                result = content.contains(pattern.get(key));
                switch (Operator)
                {
                    case "AND":
                        if (!result)
                            return false;
                        break;
                    case "OR":
                        if (result)
                            return true;
                        break;
                }
            }
            switch (Operator)
            {
                case "AND":
                    return true;

                case "OR":
                    return false;

            }
        }
        else
        {
            if(content.contains(pattern.get(1)))
                return true;
            else
                return false;
        }
        return false;
    }
    private RteStreamListener startListingPattern(final AtomicReference<String> result,final HashMap<Integer,String> pattern,final String Operator, final CountDownLatch signal) {
        final RteStreamListener listener= new RteStreamListener(){
            @Override
            public void received(final byte[] buffer) {
                System.out.println(new String(buffer));

                if (CheckPatern(buffer,pattern,Operator)||CheckStringPatern(buffer,pattern,Operator))
                {
                    rteStream.bufferClear();
                    rteStream.removeListener(this);
                    result.set(bytesToString(buffer));
                    signal.countDown();

                }

            }
        };
        rteStream.addListener(listener);
        return listener;
    }

    private String waitForListeningPattern(final RteStreamListener listener, final int timeout, final CountDownLatch l,
                                           final AtomicReference<String> result) throws RTETimeOutException {
        try {
            if (!l.await(timeout, TimeUnit.SECONDS)) {
                rteStream.removeListener(listener);
                throw new RTETimeOutException("Action has reach the timeout: "+ Integer.toString(timeout));
            }
        } catch (InterruptedException e) {
        }
        return result.get();
    }


    private void sendTextKeysWithWaitingEcho(final String text, final int timeout) throws TelnetSessionException,RTETimeOutException {
        try {
            final byte[] bytes = text.getBytes();
            final CountDownLatch latch = new CountDownLatch(1);

            rteStream.addListener(new RteStreamListener() {
                @Override
                public void received(byte[] buffer) {
                    System.out.println("receiving echo :");
                    System.out.println(new String(buffer));
                    if (RteKeys.isKeysSent(bytes, buffer)) {

                        rteStream.bufferClear();
                        rteStream.removeListener(this);
                        latch.countDown();
                    }

                }
            });

            sendTextKeysWithoutWaitingEcho(bytes);

            try {
                if (!latch.await(timeout, TimeUnit.SECONDS)) {
                    throw new RTETimeOutException("Action has reach the timeout: "+ Integer.toString(timeout));
                }
            } catch (final InterruptedException e) {
            }
        } catch (final Throwable t) {
            throw new TelnetSessionException(t);
        }
    }

    private void sendTextKeysWithoutWaitingEcho(final byte[] textAsBytes) throws TelnetSessionException {
        try {
            if(rteStream.isAlive()) {
                context.getLogger().debug("Sending characters :" + new String(textAsBytes, "UTF-8"));
                System.out.println("Sending characters :" + new String(textAsBytes, "UTF-8"));
                outputStream.write(textAsBytes);
                outputStream.flush();
            }
            else
                throw new TelnetSessionException("RTESTream not active");
        } catch (final IOException e) {
            throw new TelnetSessionException(e);
        }
    }

    public String sendSpecialKeys(final String specialText, final int timeout,boolean clearbuffer) throws TelnetSessionException, RTETimeOutException {
        if (!channel.isConnected()) throw new TelnetSessionException(new RuntimeException("Channel is already close"));

        final SpecialKeys sp = SpecialKeysConverter.INSTANCE.apply(specialText);
        try {

            //-----clear the buffer if requested----
            if(clearbuffer)
                this.rteStream.bufferClear();

            if(this.rteStream.isAlive())
            {
                outputStream.write(sp.getAsciiCode());
                if (sp.getAdditionalBytes().isPresent()) {
                    outputStream.write(sp.getAdditionalBytes().get());
                }
                outputStream.flush();
            }
            else
                throw new TelnetSessionException("RTESTream not active");

            return "";
        } catch (final IOException e) {
            throw new TelnetSessionException(e);
        }
    }

    public String sendSpecialKeysAndWaitFor(final String specialText, final HashMap<Integer,String> pattern,final String Operator ,final int timeout,boolean clearbuffer) throws TelnetSessionException, RTETimeOutException {
        if (!channel.isConnected()) throw new TelnetSessionException(new RuntimeException("Channel is already close"));

        final CountDownLatch l = new CountDownLatch(1);
        final AtomicReference<String> result = new AtomicReference<String>("");
        final RteStreamListener listener = startListingPattern(result,pattern,Operator, l);
        final SpecialKeys sp = SpecialKeysConverter.INSTANCE.apply(specialText);
        try {

            //-----clear the buffer if requested----
            if(clearbuffer)
                this.rteStream.bufferClear();

            if(this.rteStream.isAlive()) {
                outputStream.write(sp.getAsciiCode());
                if (sp.getAdditionalBytes().isPresent()) {
                    outputStream.write(sp.getAdditionalBytes().get());
                }
                outputStream.flush();
            }
            else
                throw new TelnetSessionException("RTESTream not active");

            return waitForListeningPattern(listener,timeout, l, result);
        } catch (final IOException e) {
            throw new TelnetSessionException(e);
        }
    }

    public String readUntil(final HashMap<Integer,String> pattern,final String Operator, final int timeout) throws TelnetSessionException, RTETimeOutException {
        if (!channel.isConnected()) throw new TelnetSessionException(new RuntimeException("Channel is already close"));

        final CountDownLatch l = new CountDownLatch(1);
        final AtomicReference<String> result = new AtomicReference<String>("");
        final RteStreamListener listener = startListingPattern(result,pattern,Operator, l);
        return waitForListeningPattern(listener,timeout, l, result);
    }

    public static boolean isKeyInSpecialKeys(final String key) {
        return SpecialKeysConverter.IsKeyInTheList(key);
    }

    //#TODO : Change the cleaning outptu screen
    private static String bytesToString(final byte[] buffer)
    {
        final String line = new String(buffer);
        return line
                .replaceAll("\u001B\\[[\\d;]*[^\\d;]","")
                .replaceAll("1h\u001B=","")
                .replaceAll("\u001B\\(B","")
                .replaceAll("\u001B\\)0","")
                .replaceAll("\u000F7h","")
                .replaceAll("\u000F","");
    }
}
