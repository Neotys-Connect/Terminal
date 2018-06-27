package com.neotys.rte.TerminalEmulator.ssh.rte;

public interface RteStreamListener {
	void received(byte[] buffer);
}
