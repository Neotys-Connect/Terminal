package com.neotys.rte.TerminalEmulator.telnet;

import com.google.common.base.Ascii;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TelnetChannel {
    private final TelnetClient channel;
    private final RteStream rteStream;
    private final OutputStream outputStream;
    private final Context context;

    private static final byte[] SCREEN_NAME = new byte[] {Ascii.ESC, '[', '1', ';', '1','5', 'H'};

    private static final byte[] RERESH_SCREEN = new byte[] {Ascii.ESC, '[', '2', 'J', };
    private TelnetChannel(final TelnetClient channel, Context context) throws IOException {
        this.channel = channel;
        this.rteStream = RteStream.of(TelnetSource.of(channel));
        this.outputStream = channel.getOutputStream();
        this.context=context;


    }

    public static TelnetChannel of(final TelnetClient session, Context context) throws TelnetSessionException {
        try {
            return new TelnetChannel(session,context);
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
            content=generateScreenDisplay(this.rteStream.bufferCopy());
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

                int linenumber=1;
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
               System.out.println(dtf.format(now) + " - Received characters :"+new String(buffer));
                if (CheckPatern(buffer,pattern,Operator)||CheckStringPatern(buffer,pattern,Operator))
                {
                    now = LocalDateTime.now();
                    System.out.println(dtf.format(now)+ "- Found pattern"+pattern.toString()+ "in" + new String(buffer));
                    rteStream.bufferClear();
                    rteStream.removeListener(this);
                    result.set(generateScreenDisplay(buffer));
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
                System.out.println("Sending special  characters :" + specialText);
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
                System.out.println("Sending special  characters :" + specialText);
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

    public String readUntilNew(final HashMap<Integer,String> pattern,final String Operator, final int timeout) throws TelnetSessionException, RTETimeOutException {
        if (!channel.isConnected()) throw new TelnetSessionException(new RuntimeException("Channel is already close"));

        final CountDownLatch l = new CountDownLatch(1);
        final AtomicReference<String> result = new AtomicReference<String>("");
        final RteStreamListener listener = startListingPattern(result,pattern,Operator, l);
        return waitForListeningPattern(listener,timeout, l, result);
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


    private String generateScreenDisplay(final byte[] buffer)
    {
        final String line = new String(buffer);
        final String[] split = line.split("\u001b\\[");
        final List<String> collect = Arrays.stream(split).collect(Collectors.toList());
        StringBuilder output=new StringBuilder();
        int linenumber=1;
        int column=1;
        for (final String s1 : collect)
        {
            if(s1.trim().isEmpty())
                continue;

            final int lineindex  = s1.indexOf(';');
            final int col = s1.indexOf('f');
            if(lineindex>0) {
                int lineref = Integer.parseInt(s1.substring(0, lineindex));
                int colref;
                String content;
                if (col > 0) {
                    colref = Integer.parseInt(s1.substring(lineindex + 1, col));
                    content = s1.substring(col + 1);
                } else {
                    int colour=s1.indexOf('m');
                    if(colour>0)
                    {
                        content=s1.substring(colour + 1);
                        colref=1;

                    }
                    else
                    {
                        colref = -1;
                        content = "";
                    }

                }

                if (linenumber < lineref) {
                    while (linenumber < lineref) {
                        output.append("\n");
                        linenumber++;
                        column = 1;
                    }
                }

                if (colref > 0) {
                    if (column < colref) {
                        output.append(" ");
                        column++;
                    }
                }
                output.append(content);
                column = column + content.length();
            }
            else
            {
                int colour=s1.indexOf('m');
                if(colour>0) {
                    output.append(s1.substring(colour + 1));
                }
                else {
                    output.append(s1);
                }
            }


        }


        return output.toString();

    }
}
