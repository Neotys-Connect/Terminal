package com.neotys.rte.TerminalEmulator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OpenSessionActionTest {
	@Test
	public void shouldReturnType() {
		final OpenSessionAction action = new OpenSessionAction();
		assertEquals("OpenSession", action.getType());
	}

}
