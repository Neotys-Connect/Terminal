package com.neotys.rte.TerminalEmulator.ssh;

import java.security.Security;
import java.util.Properties;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

final class SSHConnector {
	static final SSHConnector INSTANCE = new SSHConnector();

	protected Session createSession(final String host, final int port, final String username, final String password, final int timeout) throws SSHSessionException {
		try {
			final JSch jsch=new JSch();
			final Session session = jsch.getSession(username, host, port);
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");
			config.put("kex","diffie-hellman-group1-sha1,diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256");
			//config.put("diffie-hellman-group-exchange-sha1","com.jcraft.jsch.DHGEX");
			Security.insertProviderAt(new BouncyCastleProvider(), 1);
			session.setConfig(config);
			session.setPassword(password);

			session.connect(timeout*1000);

			return session;
		} catch (final JSchException e) {
			throw new SSHSessionException(e);
		}
	}

	protected ChannelShell createShellChannel(final Session session,boolean isPtyEnabled) throws SSHSessionException {
		try {
			final ChannelShell channel = (ChannelShell)session.openChannel("shell");
			if(isPtyEnabled)
			{
				//----normal mode working with ptty --- other mode for debugging showing more characters
				channel.setPtyType("dumb");
				channel.setPty(false);
			}
			channel.connect();
			return channel;
		} catch (final JSchException e) {
			throw new SSHSessionException(e);
		}
	}
}
