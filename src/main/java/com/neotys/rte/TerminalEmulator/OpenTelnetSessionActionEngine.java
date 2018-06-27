package com.neotys.rte.TerminalEmulator;

import com.google.common.base.Strings;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;
import org.apache.commons.net.telnet.TelnetClient;

import javax.management.openmbean.OpenDataException;
import java.util.List;

/**
 * Created by hrexed on 11/06/18.
 */
public class OpenTelnetSessionActionEngine implements ActionEngine {
    String Host=null;
    String Sport=null;
    int port;
    String STimeOut;
    int TimeOut;
    String TerminalType;
    @Override
    public SampleResult execute(Context context, List<ActionParameter> parameters) {
        final SampleResult sampleResult = new SampleResult();
        final StringBuilder requestBuilder = new StringBuilder();
        final StringBuilder responseBuilder = new StringBuilder();


        //sess=null;
        for(ActionParameter parameter:parameters) {
            switch(parameter.getName())
            {
                case  OpenTelnetSessionAction.HOST:
                    Host= parameter.getValue();
                    break;
                case OpenTelnetSessionAction.Port:
                    Sport = parameter.getValue();
                    break;

                case  OpenTelnetSessionAction.TimeOut:
                    STimeOut = parameter.getValue();
                    break;

                case  OpenTelnetSessionAction.TerminalType:
                    TerminalType = parameter.getValue();
                    break;
            }
        }

        if (Strings.isNullOrEmpty(Host)) {
            return getErrorResult(context, sampleResult, "Invalid argument: Host cannot be null "
                    + OpenTelnetSessionAction.HOST + ".", null);
        }
        if (Strings.isNullOrEmpty(TerminalType)) {
            return getErrorResult(context, sampleResult, "Invalid argument: TerminalType cannot be null "
                    + OpenTelnetSessionAction.TerminalType + ".", null);
        }
        if (Strings.isNullOrEmpty(Sport)) {
            return getErrorResult(context, sampleResult, "Invalid argument: port cannot be null "
                    + OpenTelnetSessionAction.Port + ".", null);
        }
        else
        {
            try{
                port=Integer.parseInt(Sport);
            }
            catch (NumberFormatException e)
            {
                return getErrorResult(context, sampleResult, "Invalid argument: port needs to be a digit "
                        + OpenTelnetSessionAction.Port + ".", null);
            }
        }
        if (Strings.isNullOrEmpty(STimeOut)) {
            return getErrorResult(context, sampleResult, "Invalid argument: TimeOut cannot be null "
                    + OpenTelnetSessionAction.TimeOut + ".", null);
        }
        else
        {
            try{
                TimeOut=Integer.parseInt(STimeOut);
            }
            catch (NumberFormatException e)
            {
                return getErrorResult(context, sampleResult, "Invalid argument: TimeOut needs to be a digit "
                        + OpenTelnetSessionAction.TimeOut + ".", null);
            }
        }



        try {
            sampleResult.sampleStart();

            TelnetClient channel = TelnetTerminalUtils.OpenSession(Host, port,TerminalType, TimeOut);

            if(channel.isConnected())
                appendLineToStringBuilder(responseBuilder, "Session open on "+Host);
            else
                return getErrorResult(context, sampleResult, "Session Error: Unable to open the session "
                        , null);


            sampleResult.sampleEnd();
            context.getCurrentVirtualUser().put( Host+"TelnetClient",channel);
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
        result.setStatusCode("NL-OpenTelnetSessionAction_ERROR");
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
