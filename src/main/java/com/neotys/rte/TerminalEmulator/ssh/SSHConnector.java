package com.neotys.rte.TerminalEmulator.ssh;

import java.util.Properties;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

final class SSHConnector {
	static final SSHConnector INSTANCE = new SSHConnector();

	protected Session createSession(final String host, final int port, final String username, final String password, final int timeout) throws SSHSessionException {
		try {
			final JSch jsch=new JSch();
			final Session session = jsch.getSession(username, host, port);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.setPassword(password);

			session.connect(timeout*1000);

			return session;
		} catch (final JSchException e) {
			throw new SSHSessionException(e);
		}
	}

	protected ChannelShell createShellChannel(final Session session) throws SSHSessionException {
		try {
			final ChannelShell channel = (ChannelShell)session.openChannel("shell");
			channel.setPtyType("dumb");
			channel.setPty(false);
			channel.connect();
			return channel;
		} catch (final JSchException e) {
			throw new SSHSessionException(e);
		}
	}
}
