package com.neotys.rte.TerminalEmulator;

import java.util.List;

import com.google.common.base.Strings;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;
import com.neotys.rte.TerminalEmulator.ssh.SSHChannel;

/**
 * Created by hrexed on 11/05/18.
 */
public class ReadActionUntilEngine implements ActionEngine {
    String Host=null;
    String Check=null;
    String Key=null;
    String STimeOut;
    int TimeOut;
    public SampleResult execute(Context context, List<ActionParameter> parameters) {
        final SampleResult sampleResult = new SampleResult();
        final StringBuilder requestBuilder = new StringBuilder();
        final StringBuilder responseBuilder = new StringBuilder();

        //sess=null;
        for(ActionParameter parameter:parameters) {
            switch(parameter.getName())
            {
                case  ReadActionUntil.HOST:
                    Host= parameter.getValue();
                    break;

                case  ReadActionUntil.TimeOut:
                    STimeOut = parameter.getValue();
                    break;
                case  ReadActionUntil.CHECK:
                    Check = parameter.getValue();
                    break;

            }
        }

        if (Strings.isNullOrEmpty(Host)) {
            return getErrorResult(context, sampleResult, "Invalid argument: Host cannot be null "
                    + ReadActionUntil.HOST + ".", null);
        }

        if (Strings.isNullOrEmpty(STimeOut)) {
            return getErrorResult(context, sampleResult, "Invalid argument: TimeOut cannot be null "
                    + ReadActionUntil.TimeOut + ".", null);
        }
        else
        {
            try{
                TimeOut=Integer.parseInt(STimeOut);
            }
            catch (NumberFormatException e)
            {
                return getErrorResult(context, sampleResult, "Invalid argument: TimeOut needs to be a digit "
                        + ReadActionUntil.TimeOut + ".", null);
            }
        }

        if (Strings.isNullOrEmpty(Check)) {
            return getErrorResult(context, sampleResult, "Invalid argument: CHECK cannot be null "
                    + ReadActionUntil.CHECK + ".", null);
        }

        try {


            final SSHChannel channel = (SSHChannel)context.getCurrentVirtualUser().get(Host+"SSHChannel");
            if(channel != null)
            {
                if (channel.isConnected())
                {
                    try
                    {
                        sampleResult.sampleStart();
                        final String output = channel.readUntil(Check, TimeOut);
                        sampleResult.sampleEnd();
                        appendLineToStringBuilder(responseBuilder, output);

                        /*if(!TerminalUtils.IsPaternInStringbuilder(Check,output))
                            return getErrorResult(context, sampleResult, "Patern not found: the patern was not found "
                                    + ReadActionUntil.CHECK + ".", null);*/


                    }
                    catch (RTETimeOutException e) {
                        return getErrorResult(context, sampleResult, "TimeOut Exception:  "
                                , null);
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
        result.setStatusCode("NL-ReadActionUntil_ERROR");
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
