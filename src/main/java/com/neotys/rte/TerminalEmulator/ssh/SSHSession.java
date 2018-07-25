package com.neotys.rte.TerminalEmulator.ssh;

import com.jcraft.jsch.Session;
import com.neotys.extensions.action.engine.Context;

public final class SSHSession {
	private final Session session;
	private Context context;
	private SSHSession(final Session session,Context context) {

		this.session = session;
		this.context=context;
	}
	
	public static SSHSession of(final String host, final int port, final String username, final String password, final int timeout,Context context) throws SSHSessionException {
		return new SSHSession(SSHConnector.INSTANCE.createSession(host, port, username, password, timeout),context);
	}
	
	public boolean isConnected() {
		return session.isConnected();
	}
	
	/**
	 * Close ALL channels and Session at the same time
	 */
	public void close() {
		session.disconnect();
	}
	
	public SSHChannel createChannel() throws SSHSessionException {
		return SSHChannel.of(session,context);
	}
}