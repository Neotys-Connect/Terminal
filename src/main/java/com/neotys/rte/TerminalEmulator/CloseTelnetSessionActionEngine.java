package com.neotys.rte.TerminalEmulator;

import com.google.common.base.Strings;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;
import com.neotys.rte.TerminalEmulator.telnet.TelnetChannel;
import org.apache.commons.net.telnet.TelnetClient;

import java.util.List;

/**
 * Created by hrexed on 26/04/18.
 */
public class CloseTelnetSessionActionEngine implements ActionEngine {
    String Host;

    public SampleResult execute(Context context, List<ActionParameter> parameters) {
        final SampleResult sampleResult = new SampleResult();
        final StringBuilder requestBuilder = new StringBuilder();
        final StringBuilder responseBuilder = new StringBuilder();
        TelnetChannel channel;


        for(ActionParameter parameter:parameters) {
            switch (parameter.getName()) {
                case CloseTelnetSessionAction.HOST:
                    Host = parameter.getValue();
                    break;
            }
        }

        if (Strings.isNullOrEmpty(Host)) {
            return getErrorResult(context, sampleResult, "Invalid argument: Host cannot be null "
                    + CloseTelnetSessionAction.HOST + ".", null);
        }

        channel = (TelnetChannel) context.getCurrentVirtualUser().get(Host+"TelnetClient");
        if(channel != null)
        {
            if(channel.isConnected())
            {
                try {
                    sampleResult.sampleStart();
                    channel.close();
                    appendLineToStringBuilder(responseBuilder, "Session Closed on "+Host);

                    sampleResult.sampleEnd();


                }
                catch (Exception e)
                {
                    return getErrorResult(context, sampleResult, "Technical Error: "+e.getMessage(), e);
                }
            }
            else
                return getErrorResult(context, sampleResult, "No session Closed on "+Host
                        + CloseTelnetSessionAction.HOST + ".", null);
        }
        else
            return getErrorResult(context, sampleResult, "No session created on "+Host
                    + CloseTelnetSessionAction.HOST + ".", null);


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
        result.setStatusCode("NL-CloseTelnetSession_ERROR");
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
