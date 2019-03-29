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
public class SendTelnetTextActionEngine implements ActionEngine    {
        String Host=null;

        String Key=null;
        String STimeOut;
        int TimeOut;
    boolean NoWaitForEcho;
    boolean ClearBufferBefore=false;
    public SampleResult execute(Context context, List<ActionParameter> parameters) {
        final SampleResult sampleResult = new SampleResult();
        final StringBuilder requestBuilder = new StringBuilder();
        final StringBuilder responseBuilder = new StringBuilder();
        String output;
        TelnetChannel channel;
        String SNoWaitForEcho = null;
        String sClearBufferBefore = null;
        //sess=null;
        for(ActionParameter parameter:parameters) {
            switch(parameter.getName())
            {
                case  SendTelnetTextAction.HOST:
                    Host= parameter.getValue();
                    break;
                case  SendTelnetTextAction.TEXT:
                    Key = parameter.getValue();
                    break;

                case  SendTelnetTextAction.TimeOut:
                    STimeOut = parameter.getValue();
                    break;
                case  SendTelnetTextAction.NoWaitForEcho:
                    SNoWaitForEcho = parameter.getValue();
                    break;
                case SendTelnetTextAction.ClearBufferBefore:
                    sClearBufferBefore=parameter.getValue();
            }
        }

        if (Strings.isNullOrEmpty(Host)) {
            return getErrorResult(context, sampleResult, "Invalid argument: Host cannot be null "
                    + SendTelnetTextAction.HOST + ".", null);
        }

        if (Strings.isNullOrEmpty(STimeOut)) {
            return getErrorResult(context, sampleResult, "Invalid argument: TimeOut cannot be null "
                    + SendTelnetTextAction.TimeOut + ".", null);
        }
        else
        {
            try{
                TimeOut=Integer.parseInt(STimeOut);
            }
            catch (NumberFormatException e)
            {
                return getErrorResult(context, sampleResult, "Invalid argument: TimeOut needs to be a digit "
                        + SendTelnetTextAction.TimeOut + ".", null);
            }
        }

        if (Strings.isNullOrEmpty(SNoWaitForEcho)) {
            NoWaitForEcho=false;
        }
        else
        {
            if(SNoWaitForEcho.equalsIgnoreCase("TRUE"))
                NoWaitForEcho=true;
            else
                NoWaitForEcho=false;
        }
        if (Strings.isNullOrEmpty(sClearBufferBefore)) {
            ClearBufferBefore=false;
        }
        else {
            if (sClearBufferBefore.equalsIgnoreCase("TRUE"))
                ClearBufferBefore = true;
            else
                ClearBufferBefore = false;
        }
        if (Strings.isNullOrEmpty(Key)) {
            return getErrorResult(context, sampleResult, "Invalid argument: Key cannot be null "
                    + SendTelnetTextAction.TEXT + ".", null);
        }

        try {


            channel = (TelnetChannel) context.getCurrentVirtualUser().get(Host+"TelnetClient");
            if(channel != null)
            {
                if (channel.isConnected())
                {
                    try
                    {
                        sampleResult.sampleStart();
                        output=channel.sendKeys(Key, TimeOut,NoWaitForEcho,ClearBufferBefore);
                        appendLineToStringBuilder(responseBuilder, output);

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
        result.setStatusCode("NL-SendTelnetKey_ERROR");
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