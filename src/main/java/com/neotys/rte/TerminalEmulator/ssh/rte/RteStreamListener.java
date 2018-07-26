package com.neotys.rte.TerminalEmulator.ssh.rte;

import com.neotys.extensions.action.engine.Context;

public interface RteStreamListener {
	 boolean isParternFound=false;


	void received(byte[] buffer);
}
