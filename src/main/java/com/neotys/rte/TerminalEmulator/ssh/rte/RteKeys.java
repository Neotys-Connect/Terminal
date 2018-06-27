package com.neotys.rte.TerminalEmulator.ssh.rte;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.google.common.base.Ascii;
import com.google.common.primitives.Bytes;

public class RteKeys {
	private static final byte[] ECHO_START_BYTES = new byte[] {Ascii.ESC, '[', '0', ';', '4', 'm', Ascii.SI};
	private static final byte[] ECHO_END_BYTES = new byte[] {Ascii.ESC, '[', 'm', Ascii.SI};
	
	public static boolean isKeysSent(final byte[] keysSent, final byte[] content) {
		try {
			// Regular echo
			final ByteArrayOutputStream keySentAsEcho = new ByteArrayOutputStream();
			for(final byte keySent: keysSent) {
				keySentAsEcho.write(ECHO_START_BYTES);
				keySentAsEcho.write(keySent);
				keySentAsEcho.write(ECHO_END_BYTES);
			}
			if (Bytes.indexOf(content, keySentAsEcho.toByteArray()) != -1) return true;
			
			// Password fields ???
			final byte[] keySentAndWildCard = new byte[keysSent.length];
			for(int i = 0; i < keysSent.length; i++) {
				keySentAndWildCard[i] = '*';
			}
			if (Bytes.indexOf(content, keySentAndWildCard) != -1) return true;
			
			return false;
		} catch (final IOException e) {
			return false;
		}
	}
}
