package com.neotys.rte.TerminalEmulator;

import com.google.common.base.Strings;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;
import com.neotys.rte.TerminalEmulator.ssh.SSHChannel;

import java.util.List;

/**
 * Created by hrexed on 26/04/18.
 */
public class GetBufferActionEngine implements ActionEngine {
    String Host;

    public SampleResult execute(Context context, List<ActionParameter> parameters) {
        final SampleResult sampleResult = new SampleResult();
        final StringBuilder requestBuilder = new StringBuilder();
        final StringBuilder responseBuilder = new StringBuilder();
        String buffercontent=null;

        for(ActionParameter parameter:parameters) {
            switch (parameter.getName()) {
                case GetBufferAction.HOST:
                    Host = parameter.getValue();
                    break;
            }
        }

        if (Strings.isNullOrEmpty(Host)) {
            return getErrorResult(context, sampleResult, "Invalid argument: Host cannot be null "
                    + GetBufferAction.HOST + ".", null);
        }

        final SSHChannel channel = (SSHChannel)context.getCurrentVirtualUser().get(Host+"SSHChannel");
        if(channel != null)
        {
            if(channel.isConnected())
            {
                try {
                    sampleResult.sampleStart();
                    buffercontent=channel.getBufferContent();
                    if(buffercontent!=null)
                        appendLineToStringBuilder(responseBuilder, buffercontent);

                    sampleResult.sampleEnd();


                }
                catch (Exception e)
                {
                    return getErrorResult(context, sampleResult, "Technical Error: "+e.getMessage(), e);
                }
            }
            else
                return getErrorResult(context, sampleResult, "No session Closed on "+Host
                        + GetBufferAction.HOST + ".", null);
        }
        else
            return getErrorResult(context, sampleResult, "No session created on "+Host
                    + GetBufferAction.HOST + ".", null);


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
        result.setStatusCode("NL-GetBufferAction-Error");
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
