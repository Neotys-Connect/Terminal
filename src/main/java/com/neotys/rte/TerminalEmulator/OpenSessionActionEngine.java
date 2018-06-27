package com.neotys.rte.TerminalEmulator;

import java.util.List;

import com.google.common.base.Strings;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;
import com.neotys.rte.TerminalEmulator.ssh.SSHChannel;
import com.neotys.rte.TerminalEmulator.ssh.SSHSession;

public final class OpenSessionActionEngine implements ActionEngine {
	String Host=null;
	String Sport=null;
	int port;
	String Username=null;
	String Password=null;
	String STimeOut;
	int TimeOut;
	@Override
	public SampleResult execute(Context context, List<ActionParameter> parameters) {
		final SampleResult sampleResult = new SampleResult();
		final StringBuilder requestBuilder = new StringBuilder();
		final StringBuilder responseBuilder = new StringBuilder();


		//sess=null;
		for(ActionParameter parameter:parameters) {
			switch(parameter.getName())
			{
				case  OpenSessionAction.HOST:
					Host= parameter.getValue();
					break;
				case  OpenSessionAction.Port:
					Sport = parameter.getValue();
					break;
				case  OpenSessionAction.UserName:
					Username = parameter.getValue();
					break;

				case  OpenSessionAction.Password:
					Password = parameter.getValue();
					break;
				case  OpenSessionAction.TimeOut:
					STimeOut = parameter.getValue();
					break;

			}
		}

		if (Strings.isNullOrEmpty(Host)) {
			return getErrorResult(context, sampleResult, "Invalid argument: Host cannot be null "
					+ OpenSessionAction.HOST + ".", null);
		}
		if (Strings.isNullOrEmpty(Sport)) {
			return getErrorResult(context, sampleResult, "Invalid argument: port cannot be null "
					+ OpenSessionAction.Port + ".", null);
		}
		else
		{
			try{
				port=Integer.parseInt(Sport);
			}
			catch (NumberFormatException e)
			{
				return getErrorResult(context, sampleResult, "Invalid argument: port needs to be a digit "
						+ OpenSessionAction.Port + ".", null);
			}
		}
		if (Strings.isNullOrEmpty(STimeOut)) {
			return getErrorResult(context, sampleResult, "Invalid argument: TimeOut cannot be null "
					+ OpenSessionAction.TimeOut + ".", null);
		}
		else
		{
			try{
				TimeOut=Integer.parseInt(STimeOut);
			}
			catch (NumberFormatException e)
			{
				return getErrorResult(context, sampleResult, "Invalid argument: TimeOut needs to be a digit "
						+ OpenSessionAction.TimeOut + ".", null);
			}
		}

		if (Strings.isNullOrEmpty(Username)) {
			return getErrorResult(context, sampleResult, "Invalid argument: Username cannot be null "
					+ OpenSessionAction.UserName + ".", null);
		}
		if (Strings.isNullOrEmpty(Password)) {
			return getErrorResult(context, sampleResult, "Invalid argument: Password cannot be null "
					+ OpenSessionAction.Password + ".", null);
		}

		try {
			sampleResult.sampleStart();

			final SSHSession session = SSHSession.of(Host, port, Username, Password, TimeOut);
			final SSHChannel channel = session.createChannel();

			if(channel.isConnected())
				appendLineToStringBuilder(responseBuilder, "Session open on "+Host);
			else
				return getErrorResult(context, sampleResult, "Session Error: Unable to open the session "
						, null);

			sampleResult.sampleEnd();
			context.getCurrentVirtualUser().put(Host+"SSHChannel", channel);
		}
		catch (Exception e)
		{
			return getErrorResult(context, sampleResult, "Technical Error: "+e.getMessage(), e);
		}
		sampleResult.setRequestContent(requestBuilder.toString());
		sampleResult.setResponseContent(responseBuilder.toString());
		return sampleResult;
	}

	private void appendLineToStringBuilder(final StringBuilder sb, final String line){
		sb.append(line).append("\n");
	}

	/**
	 * This method allows to easily create an error result and log exception.
	 */
	private static SampleResult getErrorResult(final Context context, final SampleResult result, final String errorMessage, final Exception exception) {
		result.setError(true);
		result.setStatusCode("NL-OpenSession_ERROR");
		result.setResponseContent(errorMessage);
		if(exception != null){
			context.getLogger().error(errorMessage, exception);
		} else{
			context.getLogger().error(errorMessage);
		}
		return result;
	}

	@Override
	public void stopExecute() {
		// TODO add code executed when the test have to stop.
	}

}
