package com.neotys.rte.TerminalEmulator;

import com.google.common.base.Strings;
import com.jcraft.jsch.Channel;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;

import java.util.List;

/**
 * Created by hrexed on 26/04/18.
 */
public class CloseSessionActionEngine implements ActionEngine {
    String Host;

    public SampleResult execute(Context context, List<ActionParameter> parameters) {
        final SampleResult sampleResult = new SampleResult();
        final StringBuilder requestBuilder = new StringBuilder();
        final StringBuilder responseBuilder = new StringBuilder();
        Channel channel;


        for(ActionParameter parameter:parameters) {
            switch (parameter.getName()) {
                case CloseSessionAction.HOST:
                    Host = parameter.getValue();
                    break;
            }
        }

        if (Strings.isNullOrEmpty(Host)) {
            return getErrorResult(context, sampleResult, "Invalid argument: Host cannot be null "
                    + CloseSessionAction.HOST + ".", null);
        }

        channel = (Channel)context.getCurrentVirtualUser().get(Host+"Channel");
        if(channel != null)
        {
            if(channel.isConnected())
            {
                try {
                    sampleResult.sampleStart();
                    TerminalUtils.CloseSession(channel);
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
                        + CloseSessionAction.HOST + ".", null);
        }
        else
            return getErrorResult(context, sampleResult, "No session created on "+Host
                    + CloseSessionAction.HOST + ".", null);


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
        result.setStatusCode("NL-CloseSession_ERROR");
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
