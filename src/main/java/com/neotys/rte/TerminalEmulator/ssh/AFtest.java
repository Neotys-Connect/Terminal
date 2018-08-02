package com.neotys.rte.TerminalEmulator.ssh;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

import com.google.common.base.Ascii;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.Session;
import com.neotys.rte.TerminalEmulator.ch.SpecialKeys;
import com.neotys.rte.TerminalEmulator.ch.SpecialKeysConverter;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteStream;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteStreamListener;
/**
 * Created by hrexed on 26/07/18.
 */
public class AFtest {
    private static final String HOST = "sumhrfappqa";
    private static final int PORT = 22;
    private static final String LOGIN = "rfuser";
    private static final String PASSWORD = "anf1892";

    public static void main(String[] args) throws Throwable {
        final Session s = SSHConnector.INSTANCE.createSession(HOST, PORT, LOGIN, PASSWORD, 10);
        final ChannelShell createShellChannel = SSHConnector.INSTANCE.createShellChannel(s,false);
        final RteStream stream = RteStream.of(SshRteSource.of(createShellChannel));

        try {
            stream.addListener(new RteStreamListener() {
                @Override
                public void received(byte[] buffer) {
                    System.out.println(new String(buffer));
                }
            });

            final OutputStream outputStream = createShellChannel.getOutputStream();
            final Scanner scanner = new Scanner(System.in);
            while(true) {
                final String nextLine = scanner.nextLine();
                if ("EXIT".equals(nextLine.trim())) break;

                final SpecialKeys apply = SpecialKeysConverter.INSTANCE.apply(nextLine);
                final byte[] bytes = toBytes(nextLine, apply);

                outputStream.write(bytes);
                outputStream.flush();
            }
        } finally {
            System.out.println("CLOSEDDDD");
            createShellChannel.disconnect();
            s.disconnect();
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
