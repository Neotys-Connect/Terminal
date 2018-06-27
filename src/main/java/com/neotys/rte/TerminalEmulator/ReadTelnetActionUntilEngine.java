package com.neotys.rte.TerminalEmulator;

import com.google.common.base.Strings;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;
import org.apache.commons.net.telnet.TelnetClient;

import java.util.List;

/**
 * Created by hrexed on 11/05/18.
 */
public class ReadTelnetActionUntilEngine implements ActionEngine {
    String Host=null;
    String Check=null;
    String Key=null;
    String STimeOut;
    int TimeOut;
    public SampleResult execute(Context context, List<ActionParameter> parameters) {
        final SampleResult sampleResult = new SampleResult();
        final StringBuilder requestBuilder = new StringBuilder();
        final StringBuilder responseBuilder = new StringBuilder();
        StringBuilder output;
        TelnetClient channel;

        //sess=null;
        for(ActionParameter parameter:parameters) {
            switch(parameter.getName())
            {
                case  ReadTelnetActionUntil.HOST:
                    Host= parameter.getValue();
                    break;

                case  ReadTelnetActionUntil.TimeOut:
                    STimeOut = parameter.getValue();
                    break;
                case  ReadTelnetActionUntil.CHECK:
                    Check = parameter.getValue();
                    break;

            }
        }

        if (Strings.isNullOrEmpty(Host)) {
            return getErrorResult(context, sampleResult, "Invalid argument: Host cannot be null "
                    + ReadTelnetActionUntil.HOST + ".", null);
        }

        if (Strings.isNullOrEmpty(STimeOut)) {
            return getErrorResult(context, sampleResult, "Invalid argument: TimeOut cannot be null "
                    + ReadTelnetActionUntil.TimeOut + ".", null);
        }
        else
        {
            try{
                TimeOut=Integer.parseInt(STimeOut);
            }
            catch (NumberFormatException e)
            {
                return getErrorResult(context, sampleResult, "Invalid argument: TimeOut needs to be a digit "
                        + ReadTelnetActionUntil.TimeOut + ".", null);
            }
        }

        if (Strings.isNullOrEmpty(Check)) {
            return getErrorResult(context, sampleResult, "Invalid argument: CHECK cannot be null "
                    + ReadTelnetActionUntil.CHECK + ".", null);
        }

        try {


            channel = (TelnetClient) context.getCurrentVirtualUser().get(Host+"TelnetClient");
            if(channel != null)
            {
                if (channel.isConnected())
                {
                    try
                    {
                        sampleResult.sampleStart();
                        output=TelnetTerminalUtils.ReadUntil(channel,Check,TimeOut);

                        appendLineToStringBuilder(responseBuilder, output.toString());

                        sampleResult.sampleEnd();

                       /* if(!TerminalUtils.IsPaternInStringbuilder(Check,output))
                            return getErrorResult(context, sampleResult, "Patern not found: the patern was not found "
                                    + ReadTelnetActionUntil.CHECK + ".", null);*/


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
        result.setStatusCode("NL-ReadTelnetActionUntil_ERROR");
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
