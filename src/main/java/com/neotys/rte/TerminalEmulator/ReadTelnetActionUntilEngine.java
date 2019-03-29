package com.neotys.rte.TerminalEmulator;

import com.google.common.base.Strings;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;
import com.neotys.extensions.action.engine.Context;
import com.neotys.extensions.action.engine.SampleResult;
import com.neotys.rte.TerminalEmulator.telnet.TelnetChannel;
import org.apache.commons.net.telnet.TelnetClient;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hrexed on 11/05/18.
 */
public class ReadTelnetActionUntilEngine implements ActionEngine {
    String Host=null;
    String Check=null;
    String Key=null;
    String STimeOut;
    String OPERATOR=null;
    int TimeOut;
    public SampleResult execute(Context context, List<ActionParameter> parameters) {
        final SampleResult sampleResult = new SampleResult();
        final StringBuilder requestBuilder = new StringBuilder();
        final StringBuilder responseBuilder = new StringBuilder();
        StringBuilder output;
        TelnetChannel channel;
        String outputscreen;
        String pattern = "CHECK(\\d+)";
        Pattern reg = Pattern.compile(pattern);
        HashMap< Integer,String> CHECKList;
        CHECKList = new HashMap< Integer,String>();
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
                case  ReadTelnetActionUntil.OPERATOR:
                    OPERATOR = parameter.getValue();
                    break;
                case  "CHECK":
                    CHECKList.put(1,parameter.getValue());
                    break;
                default:
                    Matcher m = reg.matcher(parameter.getName());
                    if (m.find( ))
                    {
                        CHECKList.put(Integer.valueOf(m.group(1)),parameter.getValue());
                    }

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

        if(CHECKList.isEmpty()) {

            return getErrorResult(context, sampleResult, "Invalid argument: you need at least One check "
                    + ReadActionUntil.CHECK1 + ".", null);

        }
        else
        {
            for(int keys: CHECKList.keySet())
            {
                if (Strings.isNullOrEmpty(CHECKList.get(keys)))
                {
                    return getErrorResult(context, sampleResult, "Invalid argument: CHECK"+keys+ " cannot be null"
                            + ReadActionUntil.CHECK1 + ".", null);
                }

            }
            if(CHECKList.size()>1)
            {
                if (Strings.isNullOrEmpty(OPERATOR)) {
                    return getErrorResult(context, sampleResult, "Invalid argument: OPERATOR cannot be null if you more than one CHECK"
                            + ReadActionUntil.OPERATOR + ".", null);
                }
                else
                {
                    if( !(OPERATOR.equalsIgnoreCase("AND") || OPERATOR.equalsIgnoreCase("OR")))
                    {
                        return getErrorResult(context, sampleResult, "Invalid argument: OPERATOR can only have the value AND or OR"
                                + ReadActionUntil.OPERATOR + ".", null);
                    }
                }
            }

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
                        outputscreen=channel.readUntil(CHECKList,OPERATOR, TimeOut);

                        appendLineToStringBuilder(responseBuilder,outputscreen);

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
