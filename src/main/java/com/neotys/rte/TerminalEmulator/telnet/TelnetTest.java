package com.neotys.rte.TerminalEmulator.telnet;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Ascii;

import com.neotys.rte.TerminalEmulator.ch.SpecialKeys;
import com.neotys.rte.TerminalEmulator.ch.SpecialKeysConverter;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteStream;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteStreamListener;

import org.apache.commons.net.telnet.TelnetClient;

/**
 * Created by hrexed on 26/07/18.
 */
public class TelnetTest {
    private static final String HOST = "FRMEUWNBXTSO1";
    private static final int PORT = 23;
    private static final String LOGIN = "INFFLUX";
    private static int timeout=600;

    public static void main(String[] args) throws Throwable {
        final TelnetClient telnetClient = (TelnetClient) TelnetConnector.INSTANCE.createSession(HOST, PORT,"VT100",timeout );


        OutputStream outputStream = telnetClient.getOutputStream();

        try {

            InputStream instr = telnetClient.getInputStream();


                byte[] buff = new byte[1024];
                int ret_read = 0;


                ret_read = instr.read(buff);
                if(ret_read > 0)
                {
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write(LOGIN.getBytes());
                    outputStream.flush();
                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write(SpecialKeys.of(Ascii.CR).getAsciiCode());
                    outputStream.write(SpecialKeys.of(Ascii.LF).getAsciiCode());

                    outputStream.flush();
                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write(SpecialKeys.of(Ascii.CR).getAsciiCode());
                    outputStream.write(SpecialKeys.of(Ascii.LF).getAsciiCode());

                    outputStream.flush();
                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write("1".getBytes());
                    outputStream.flush();
                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write(SpecialKeys.of(Ascii.CR).getAsciiCode());
                    outputStream.write(SpecialKeys.of(Ascii.LF).getAsciiCode());
                    outputStream.flush();

                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write( SpecialKeysConverter.INSTANCE.apply("F2").getAsciiCode());
                    outputStream.flush();
                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write(SpecialKeys.of(Ascii.CR).getAsciiCode());
                    outputStream.write(SpecialKeys.of(Ascii.LF).getAsciiCode());
                    outputStream.flush();
                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write("4".getBytes());
                    outputStream.flush();
                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write(SpecialKeys.of(Ascii.CR).getAsciiCode());
                    outputStream.write(SpecialKeys.of(Ascii.LF).getAsciiCode());
                    outputStream.flush();
                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write("123154".getBytes());
                    outputStream.flush();
                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write(SpecialKeys.of(Ascii.CR).getAsciiCode());
                    outputStream.write(SpecialKeys.of(Ascii.LF).getAsciiCode());
                    outputStream.flush();
                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write(SpecialKeys.of(Ascii.ESC).getAsciiCode());
                    outputStream.write(SpecialKeys.of(Ascii.CR).getAsciiCode());
                    outputStream.flush();
                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write(SpecialKeys.of(Ascii.ESC).getAsciiCode());
                    outputStream.write(SpecialKeys.of(Ascii.CR).getAsciiCode());
                    outputStream.flush();
                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write(SpecialKeys.of(Ascii.ESC).getAsciiCode());
                    outputStream.write(SpecialKeys.of(Ascii.CR).getAsciiCode());
                    outputStream.flush();
                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");
                    outputStream.write(SpecialKeys.of(Ascii.ESC).getAsciiCode());
                    outputStream.write(SpecialKeys.of(Ascii.CR).getAsciiCode());
                    outputStream.flush();
                    ret_read = instr.read(buff);
                    System.out.println("screen:");
                    System.out.println(new String(buff, 0, ret_read)+"\n");

                    }



        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            System.out.println("CLOSEDDDD");
            telnetClient.disconnect();

            System.exit(0);
        }
    }

    private static  byte[] toBytes(final String nextLine, final SpecialKeys apply) throws IOException {
        if (!apply.getAdditionalBytes().isPresent() && apply.getAsciiCode() == Ascii.ESC) {
            return nextLine.getBytes();
        } else {
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(apply.getAsciiCode());
            if (apply.getAdditionalBytes().isPresent()) {
                byteArrayOutputStream.write(apply.getAdditionalBytes().get());
            }
            return byteArrayOutputStream.toByteArray();
        }
    }
}
