package com.neotys.rte.TerminalEmulator.ssh;

import com.jcraft.jsch.Session;

public final class SSHSession {
	private final Session session;
	
	private SSHSession(final Session session) {
		this.session = session;
	}
	
	public static SSHSession of(final String host, final int port, final String username, final String password, final int timeout) throws SSHSessionException {
		return new SSHSession(SSHConnector.INSTANCE.createSession(host, port, username, password, timeout));
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
		return SSHChannel.of(session);
	}
}