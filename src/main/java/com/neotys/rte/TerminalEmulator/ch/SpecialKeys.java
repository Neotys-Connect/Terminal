package com.neotys.rte.TerminalEmulator.ch;

import java.util.Optional;

public final class SpecialKeys {
	private final byte asciiCode;
	private final Optional<byte[]> additionalBytes;
	
	private SpecialKeys(final byte asciiCode, final Optional<byte[]> additionalBytes) {
		super();
		this.asciiCode = asciiCode;
		this.additionalBytes = additionalBytes;
	}
	
	protected static SpecialKeys of(final byte asciiCode, final byte[] additionalBytes) {
		return new SpecialKeys(asciiCode, Optional.ofNullable(additionalBytes));
	}
	
public static SpecialKeys of(final byte asciiCode) {
		return new SpecialKeys(asciiCode, Optional.empty());
	}

	public static SpecialKeys of(final byte asciiCode,final byte asciicodes) {
		byte[] converte=new byte[1];
		converte[0]=asciicodes;
		return new SpecialKeys(asciiCode, Optional.ofNullable(converte));
	}

	public byte getAsciiCode() {
		return asciiCode;
	}
	
	public Optional<byte[]> getAdditionalBytes() {
		return additionalBytes;
	}
}
