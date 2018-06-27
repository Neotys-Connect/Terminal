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
	
	protected static SpecialKeys of(final byte asciiCode) {
		return new SpecialKeys(asciiCode, Optional.empty());
	}
	
	public byte getAsciiCode() {
		return asciiCode;
	}
	
	public Optional<byte[]> getAdditionalBytes() {
		return additionalBytes;
	}
}
