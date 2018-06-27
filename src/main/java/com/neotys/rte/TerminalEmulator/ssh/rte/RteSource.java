package com.neotys.rte.TerminalEmulator.ssh.rte;

import java.io.IOException;

public interface RteSource {
	/**
	 * Returns true if source is still alive
	 * @return
	 */
	boolean isAlive();
	
	/**
	 * Blocking read returning bytes from source
	 * @return
	 * @throws IOException
	 */
	byte[] read() throws IOException;
}
