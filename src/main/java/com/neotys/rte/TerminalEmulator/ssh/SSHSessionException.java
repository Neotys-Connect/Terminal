package com.neotys.rte.TerminalEmulator.ssh;

public final class SSHSessionException extends Exception {
	private static final long serialVersionUID = -3289594500081605811L;

	public SSHSessionException(final Throwable cause) {
		super(cause);
	}

	public SSHSessionException(String text) {
		super(text);
	}
}
