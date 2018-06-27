package com.neotys.rte.TerminalEmulator.ssh;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.primitives.Bytes;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.neotys.rte.TerminalEmulator.RTETimeOutException;
import com.neotys.rte.TerminalEmulator.ch.SpecialKeys;
import com.neotys.rte.TerminalEmulator.ch.SpecialKeysConverter;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteKeys;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteStream;
import com.neotys.rte.TerminalEmulator.ssh.rte.RteStreamListener;

public final class SSHChannel {
	private final Channel channel;
	private final RteStream rteStream;
	private final OutputStream outputStream;

	private SSHChannel(final Channel channel) throws IOException {
		this.channel = channel;
		this.rteStream = RteStream.of(SshRteSource.of(channel));
		this.outputStream = channel.getOutputStream();
	}

	protected static SSHChannel of(final Session session) throws SSHSessionException {
		try {
			return new SSHChannel(SSHConnector.INSTANCE.createShellChannel(session));
		} catch (IOException e) {
			throw new SSHSessionException(e);
		}
	}
	
	public boolean isConnected() {
		return channel.isConnected();
	}
	
	public void close() {
		try {
			rteStream.close();
		} catch (IOException e) {
		}
		channel.disconnect();
		try {
			channel.getSession().disconnect();
		} catch (JSchException e) {
		}
	}
	
	public String sendKeys(final String text, final int timeout) throws SSHSessionException, RTETimeOutException {
		if (!channel.isConnected()) throw new SSHSessionException(new RuntimeException("Channel is already close"));

		sendTextKeysWithWaitingEcho(text, timeout);
		return "";
	}

	public String sendKeysAndWaitFor(final String text, final String pattern, final int timeout) throws SSHSessionException, RTETimeOutException {
		if (!channel.isConnected()) throw new SSHSessionException(new RuntimeException("Channel is already close"));

		final CountDownLatch l = new CountDownLatch(1);
		final AtomicReference<String> result = startListingPattern(pattern, l);
		
		sendTextKeysWithoutWaitingEcho(text.getBytes());
		
		return waitForListeningPattern(timeout, l, result);
	}

	private AtomicReference<String> startListingPattern(final String pattern, final CountDownLatch signal) {
		final AtomicReference<String> result = new AtomicReference<String>("");
		final byte[] patternAsBytes = pattern.getBytes();
		rteStream.addListener(new RteStreamListener() {
			@Override
			public void received(final byte[] buffer) {
				if (Bytes.indexOf(buffer, patternAsBytes) != -1) {
					rteStream.bufferClear();
					rteStream.removeListener(this);
					result.set(bytesToString(buffer));
					signal.countDown();
				}
			}
		});
		return result;
	}
	
	private String waitForListeningPattern(final int timeout, final CountDownLatch l,
			final AtomicReference<String> result) throws RTETimeOutException {
		try {
			if (!l.await(timeout, TimeUnit.SECONDS)) {
				throw new RTETimeOutException("Action has reach the timeout: "+ Integer.toString(timeout));
			}
		} catch (InterruptedException e) {
		}
		return result.get();
	}


	private void sendTextKeysWithWaitingEcho(final String text, final int timeout) throws SSHSessionException,RTETimeOutException {
		try {
			final byte[] bytes = text.getBytes();
			final CountDownLatch latch = new CountDownLatch(1);
			
			rteStream.addListener(new RteStreamListener() {
				@Override
				public void received(byte[] buffer) {
					if (RteKeys.isKeysSent(bytes, buffer)) {
						rteStream.bufferClear();
						rteStream.removeListener(this);
						latch.countDown();
					}
				}
			});
			
			sendTextKeysWithoutWaitingEcho(bytes);
			
			try {
				if (!latch.await(timeout, TimeUnit.SECONDS)) {
					throw new RTETimeOutException("Action has reach the timeout: "+ Integer.toString(timeout));
				}
			} catch (final InterruptedException e) {
			}
		} catch (final Throwable t) {
			throw new SSHSessionException(t);
		}
	}
	
	private void sendTextKeysWithoutWaitingEcho(final byte[] textAsBytes) throws SSHSessionException {
		try {
			outputStream.write(textAsBytes);
			outputStream.flush();
		} catch (final IOException e) {
			throw new SSHSessionException(e);
		}
	}
	
	public String sendSpecialKeys(final String specialText, final int timeout) throws SSHSessionException, RTETimeOutException {
		if (!channel.isConnected()) throw new SSHSessionException(new RuntimeException("Channel is already close"));

		final SpecialKeys sp = SpecialKeysConverter.INSTANCE.apply(specialText);
		try {
			outputStream.write(sp.getAsciiCode());
			if (sp.getAdditionalBytes().isPresent()) {
				outputStream.write(sp.getAdditionalBytes().get());
			}
			outputStream.flush();
			return "";
		} catch (final IOException e) {
			throw new SSHSessionException(e);
		}
	}
	
	public String sendSpecialKeysAndWaitFor(final String specialText, final String pattern, final int timeout) throws SSHSessionException, RTETimeOutException {
		if (!channel.isConnected()) throw new SSHSessionException(new RuntimeException("Channel is already close"));

		final CountDownLatch l = new CountDownLatch(1);
		final AtomicReference<String> result = startListingPattern(pattern, l);
		final SpecialKeys sp = SpecialKeysConverter.INSTANCE.apply(specialText);
		try {
			outputStream.write(sp.getAsciiCode());
			if (sp.getAdditionalBytes().isPresent()) {
				outputStream.write(sp.getAdditionalBytes().get());
			}
			outputStream.flush();
			
			return waitForListeningPattern(timeout, l, result);
		} catch (final IOException e) {
			throw new SSHSessionException(e);
		}
	}

	public String readUntil(final String pattern, final int timeout) throws SSHSessionException, RTETimeOutException {
		if (!channel.isConnected()) throw new SSHSessionException(new RuntimeException("Channel is already close"));
		
		final CountDownLatch l = new CountDownLatch(1);
		final AtomicReference<String> result = startListingPattern(pattern, l);
		return waitForListeningPattern(timeout, l, result);
	}
	
	public static boolean isKeyInSpecialKeys(final String key) {
		return SpecialKeysConverter.IsKeyInTheList(key);
	}
	
	private static String bytesToString(final byte[] buffer)
	{
		final String line = new String(buffer);
		return line
			.replaceAll("\u001B\\[[\\d;]*[^\\d;]","")
			.replaceAll("1h\u001B=","")
			.replaceAll("\u001B\\(B","")
			.replaceAll("\u001B\\)0","")
			.replaceAll("\u000F7h","")
			.replaceAll("\u000F","");
	}
}
