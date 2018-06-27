package com.neotys.rte.TerminalEmulator.ssh.rte;

import java.io.Closeable;

public interface RteStream extends Closeable {
	static RteStream of(final RteSource source) {
		return DefaultRteStream.of(source);
	}
	
	boolean isAlive();
	
	byte[] bufferClear();
	
	boolean bufferContains(byte[]... masks);
	
	byte[] bufferCopy();
	
	void addListener(RteStreamListener listener);
	
	void removeListener(RteStreamListener listener);
}
