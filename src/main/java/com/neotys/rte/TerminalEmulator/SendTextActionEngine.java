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
public class SendTextActionEngine implements ActionEngine    {
        String Host=null;

        String Key=null;
        String STimeOut;
        int TimeOut;
    public SampleResult execute(Context context, List<ActionParameter> parameters) {
        final SampleResult sampleResult = new SampleResult();
        final StringBuilder requestBuilder = new StringBuilder();
        final StringBuilder responseBuilder = new StringBuilder();
        StringBuilder output;
        Channel channel;

        //sess=null;
        for(ActionParameter parameter:parameters) {
            switch(parameter.getName())
            {
                case  SendTextAction.HOST:
                    Host= parameter.getValue();
                    break;
                case  SendTextAction.TEXT:
                    Key = parameter.getValue();
                    break;

                case  SendSpecialKeyAction.TimeOut:
                    STimeOut = parameter.getValue();
                    break;

            }
        }

        if (Strings.isNullOrEmpty(Host)) {
            return getErrorResult(context, sampleResult, "Invalid argument: Host cannot be null "
                    + SendTextAction.HOST + ".", null);
        }

        if (Strings.isNullOrEmpty(STimeOut)) {
            return getErrorResult(context, sampleResult, "Invalid argument: TimeOut cannot be null "
                    + SendTextAction.TimeOut + ".", null);
        }
        else
        {
            try{
                TimeOut=Integer.parseInt(STimeOut);
            }
            catch (NumberFormatException e)
            {
                return getErrorResult(context, sampleResult, "Invalid argument: TimeOut needs to be a digit "
                        + SendTextAction.TimeOut + ".", null);
            }
        }


        if (Strings.isNullOrEmpty(Key)) {
            return getErrorResult(context, sampleResult, "Invalid argument: Key cannot be null "
                    + SendTextAction.TEXT + ".", null);
        }

        try {


            channel = (Channel)context.getCurrentVirtualUser().get(Host+"Channel");
            if(channel != null)
            {
                if (channel.isConnected())
                {
                    try
                    {
                        sampleResult.sampleStart();
                        output=TerminalUtils.SendKeys(channel,Key,TimeOut);
                        appendLineToStringBuilder(responseBuilder, output.toString());

                        sampleResult.sampleEnd();
                    }
                    catch (Exception e) {
                        return getErrorResult(context, sampleResult, "Technical Error:  "
                                , e);
                    }
                }
                else
                    return getErrorResult(context, sampleResult, "Session Error: The session is currently closed "
                            , null);
            }
            else
                return getErrorResult(context, sampleResult, "Session Error: No session created on that host "
                        , null);

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
        result.setStatusCode("NL-SendKey_ERROR");
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