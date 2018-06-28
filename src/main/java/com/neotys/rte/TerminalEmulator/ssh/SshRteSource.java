package com.neotys.rte.TerminalEmulator.ssh;

import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jsch.Channel;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteSource;

final class SshRteSource implements RteSource {
	private final InputStream inputChannel;
	private final Channel channel;
	private final byte[] buffer = new byte[1024];
	
	private SshRteSource(final Channel channel) throws IOException {
		super();
		this.inputChannel = channel.getInputStream();
		this.channel = channel;
	}
	
	public static RteSource of(final Channel channel) throws IOException {
		return new SshRteSource(channel);
	}

	@Override
	public boolean isAlive() {
		return channel.isConnected() && !channel.isClosed() && !channel.isEOF();
	}
	
	@Override
	public byte[] read() throws IOException {
		final int read = inputChannel.read(buffer, 0, buffer.length);
		if(read==-1)
			return null;

		final byte[] result = new byte[read];
		System.arraycopy(buffer, 0, result, 0, read);
		return result;
	}
}
