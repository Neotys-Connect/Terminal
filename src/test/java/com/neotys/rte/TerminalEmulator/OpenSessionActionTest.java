package com.neotys.rte.TerminalEmulator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OpenSessionActionTest {
	@Test
	public void shouldReturnType() {
		final OpenSessionAction action = new OpenSessionAction();
		assertEquals("OpenSession", action.getType());
	}


	private String generateScreenDisplay(final byte[] buffer)
	{
		final String line = new String(buffer);
		final String[] split = line.split("\u001b\\[");
		final List<String> collect = Arrays.stream(split).collect(Collectors.toList());
		StringBuilder output=new StringBuilder();
		int linenumber=1;
		int column=1;
		for (final String s1 : collect)
		{
			if(s1.trim().isEmpty())
				continue;

			final int lineindex  = s1.indexOf(';');
			final int col = s1.indexOf('f');
			if(lineindex>0) {
				int lineref = Integer.parseInt(s1.substring(0, lineindex));
				int colref;
				String content;
				if (col > 0) {
					colref = Integer.parseInt(s1.substring(lineindex + 1, col));
					content = s1.substring(col + 1);
				} else {
					int colour=s1.indexOf('m');
					if(colour>0)
					{
					//	content=s1.substring()
						content=s1.substring(colour + 1);
						colref=1;

					}
					else
					{
						colref = -1;
						content = "";
					}

				}

				if (linenumber < lineref) {
					while (linenumber < lineref) {
						output.append("\n");
						linenumber++;
						column = 1;
					}
				}

				if (colref > 0) {
					if (column < colref) {
						output.append(" ");
						column++;
					}
				}
				output.append(content);
				column = column + content.length();
			}
			else
			{
				int colour=s1.indexOf('m');
				if(colour>0) {
					//	content=s1.substring()
					output.append(s1.substring(colour + 1));
				}
				else {
					output.append(s1);
				}
			}


		}


		return output.toString();

	}

	@Test
	public void testDIplay()
	{
		String bytereceveid="\u001B[01;32madmin@DiskStation\u001B[00m:\u001B[01; ";
		String output=generateScreenDisplay(bytereceveid.getBytes());
	}
}
