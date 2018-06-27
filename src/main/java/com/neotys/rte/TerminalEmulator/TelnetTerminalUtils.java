package com.neotys.rte.TerminalEmulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.telnet.TelnetClient;

import com.google.common.base.Ascii;
import com.neotys.rte.TerminalEmulator.ch.SpecialKeysConverter;

/**
 * Created by hrexed on 11/06/18.
 */
public class TelnetTerminalUtils {

    static TelnetClient telnetClient;
    private static String TermType;

    private static OutputStream out;
    private static InputStream in;

    public static TelnetClient OpenSession(String Host,int port, String TerminalType,int timeout) throws IOException {
        TelnetClient telnetClient=new TelnetClient(TerminalType);

        telnetClient.setDefaultTimeout(timeout);

        telnetClient.connect(Host,port);

        return telnetClient;
    }

    private static StringBuilder  readChannelOutput(TelnetClient channel,InputStream in,int timeout) throws IOException, RTETimeOutException {
        byte[] buffer = new byte[1024];
        StringBuilder result= new StringBuilder();
        long t= System.currentTimeMillis();
        long end = t+(timeout*1000);

        String line = "";
        int i;
        // BufferedReader br = new BufferedReader(new InputStreamReader(in));

       /* while((i = in.read(buffer, 0, 1024))!=-1)
        {

            line = new String(buffer, 0, i);
            line=CleanOutput(line);
            result.append(line+"\n");

            if (channel.isClosed()){
                break;
            }

            if(System.currentTimeMillis() < end)
                break;

        }*/
        if(System.currentTimeMillis() > end)
        {
            throw new RTETimeOutException("Action has reach the timeout"+ Integer.toString(timeout));
        }

        while (( i = in.read(buffer, 0, 1024) ) !=-1)
        {

            line = new String(buffer, 0, i);
            //line = CleanOutput(line);
            result.append(line + "\n");


            if(System.currentTimeMillis() > end)
            {
                throw new RTETimeOutException("Action has reach the timeout"+ Integer.toString(timeout));
            }

        }

        return result;
    }
    public static StringBuilder ReadUntil(TelnetClient telnetClient,String Check,int timeout) throws IOException, RTETimeOutException {
        StringBuilder result;
        OutputStream out;
        InputStream in;

        in=telnetClient.getInputStream();
        result= readChannelOutput(telnetClient,in,Check,timeout);

        return result;
    }
    private static StringBuilder  readChannelOutput(TelnetClient channel,InputStream in,String Check,int timeout) throws IOException, RTETimeOutException {
        byte[] buffer = new byte[1024];
        StringBuilder result= new StringBuilder();
        long t= System.currentTimeMillis();
        long end = t+(timeout*1000);


        //BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String line = "";
        int i;


        if(System.currentTimeMillis() > end) {
            throw new RTETimeOutException("Action has reach the timeout"+ Integer.toString(timeout));
        }
           /* while (( i = in.read(buffer, 0, 1024) ) !=-1)
            {

                line = new String(buffer, 0, i);
                line = CleanOutput(line);
                result.append(line + "\n");


                if (channel.isClosed()){
                    break;
                }



            }*/
        while (( i = in.read(buffer, 0, 1024) ) !=-1)
        {

            line = new String(buffer, 0, i);
            line = CleanOutput(line);
            result.append(line + "\n");

            if(System.currentTimeMillis() > end)
            {
                throw new RTETimeOutException("Action has reach the timeout"+ Integer.toString(timeout));
            }

        }

        return result;
    }
    public static String CleanOutput(String line)
    {
        line=line.replaceAll("\u001B\\[[\\d;]*[^\\d;]","");
        line=line.replaceAll("1h\u001B=","");
        line=line.replaceAll("\u001B\\(B","");
        line=line.replaceAll("\u001B\\)0","");
        line=line.replaceAll("\u000F7h","");
        line=line.replaceAll("\u000F","");

        return line;
    }

    public static StringBuilder SendKeysAndWait(TelnetClient channel,byte Text,int timeout,String patern) throws IOException, RTETimeOutException {
        StringBuilder result;
        OutputStream out;
        InputStream in;

        out=channel.getOutputStream();
        in=channel.getInputStream();

        out.write(Text);
        out.flush();

        result=readChannelOutput(channel,in,patern,timeout);

        return result;

    }

    public static StringBuilder SendKeys(TelnetClient channel,byte Text,int timeout) throws IOException, RTETimeOutException {
        StringBuilder result;
        OutputStream out;
        InputStream in;

        out=channel.getOutputStream();
        in=channel.getInputStream();

        out.write(Text);
        out.flush();

        result=readChannelOutput(channel,in,timeout);

        return result;

    }
    public static StringBuilder SendKeys(TelnetClient channel,String Text,int timeout) throws IOException, RTETimeOutException {
        StringBuilder result;
        OutputStream out;
        InputStream in;

        out=channel.getOutputStream();
        in=channel.getInputStream();

        out.write(Text.getBytes());
        out.flush();

        result=readChannelOutput(channel,in,timeout);

        return result;

    }
    public static StringBuilder SendNarrowKeys(TelnetClient channel,String Text,int timeout) throws IOException, RTETimeOutException {
        StringBuilder result;
        OutputStream out;
        InputStream in;

        out=channel.getOutputStream();
        in=channel.getInputStream();

        out.write(Ascii.ESC);

        switch (Text)
        {
            case SpecialKeysConverter.UP:
                out.write("[A".getBytes());

                break;
            case SpecialKeysConverter.DOWN:
                out.write("[B".getBytes());

                break;
            case SpecialKeysConverter.LEFT:
                out.write("[D".getBytes());

                break;
            case SpecialKeysConverter.RIGHT:
                out.write("[C".getBytes());

                break;
        }
        out.flush();

        result=readChannelOutput(channel,in,timeout);

        return result;

    }
    public static StringBuilder SendNarrowKeysandWait(TelnetClient channel,String Text,int timeout,String patern) throws IOException, RTETimeOutException {
        StringBuilder result;
        OutputStream out;
        InputStream in;

        out=channel.getOutputStream();
        in=channel.getInputStream();

        out.write(Ascii.ESC);

        switch (Text)
        {
            case SpecialKeysConverter.UP:
                out.write("[A".getBytes());

                break;
            case SpecialKeysConverter.DOWN:
                out.write("[B".getBytes());

                break;
            case SpecialKeysConverter.LEFT:
                out.write("[D".getBytes());

                break;
            case SpecialKeysConverter.RIGHT:
                out.write("[C".getBytes());

                break;
        }
        out.flush();

        result=readChannelOutput(channel,in,patern,timeout);


        return result;

    }


    public static StringBuilder SendSpecialKeysAndWaitFor(TelnetClient channel,String Text,int timeout,String patern) throws IOException, RTETimeOutException {
        switch (Text.toUpperCase())
        {
            case SpecialKeysConverter.CR:
                return  SendKeysAndWait(channel,Ascii.CR,timeout,patern);

            case SpecialKeysConverter.ESC:
                return  SendKeysAndWait(channel,Ascii.ESC,timeout,patern);

            case SpecialKeysConverter.VT:
                return  SendKeysAndWait(channel,Ascii.VT,timeout,patern);

            case SpecialKeysConverter.BS:
                return  SendKeysAndWait(channel,Ascii.BS,timeout,patern);

            case SpecialKeysConverter.HT:
                return  SendKeysAndWait(channel,Ascii.HT,timeout,patern);

            case SpecialKeysConverter.DEL:
                return  SendKeysAndWait(channel,Ascii.DEL,timeout,patern);

            case SpecialKeysConverter.LF:
                return  SendKeysAndWait(channel,Ascii.LF,timeout,patern);
            case SpecialKeysConverter.CTRLA:
                return  SendKeysAndWait(channel,Ascii.SOH,timeout,patern);
            case SpecialKeysConverter.CTRLB:
                return  SendKeysAndWait(channel,Ascii.STX,timeout,patern);
            case SpecialKeysConverter.CTRLC:
                return  SendKeysAndWait(channel,Ascii.ETX,timeout,patern);
            case SpecialKeysConverter.CTRLD:
                return  SendKeysAndWait(channel,Ascii.EOT,timeout,patern);
            case SpecialKeysConverter.CTRLE:
                return  SendKeysAndWait(channel,Ascii.ENQ,timeout,patern);
            case SpecialKeysConverter.CTRLF:
                return  SendKeysAndWait(channel,Ascii.ACK,timeout,patern);
            case SpecialKeysConverter.CTRLG:
                return  SendKeysAndWait(channel,Ascii.BEL,timeout,patern);
            case SpecialKeysConverter.CTRLH:
                return  SendKeysAndWait(channel,Ascii.BS,timeout,patern);
            case SpecialKeysConverter.CTRLI:
                return  SendKeysAndWait(channel,Ascii.HT,timeout,patern);
            case SpecialKeysConverter.CTRLJ:
                return  SendKeysAndWait(channel,Ascii.LF,timeout,patern);
            case SpecialKeysConverter.CTRLK:
                return  SendKeysAndWait(channel,Ascii.VT,timeout,patern);
            case SpecialKeysConverter.CTRLL:
                return  SendKeysAndWait(channel,Ascii.FF,timeout,patern);
            case SpecialKeysConverter.CTRLM:
                return  SendKeysAndWait(channel,Ascii.CR,timeout,patern);
            case SpecialKeysConverter.CTRLN:
                return  SendKeysAndWait(channel,Ascii.SO,timeout,patern);
            case SpecialKeysConverter.CTRLO:
                return  SendKeysAndWait(channel,Ascii.SI,timeout,patern);
            case SpecialKeysConverter.CTRLP:
                return  SendKeysAndWait(channel,Ascii.DLE,timeout,patern);
            case SpecialKeysConverter.CTRLQ:
                return  SendKeysAndWait(channel,Ascii.DC1,timeout,patern);
            case SpecialKeysConverter.CTRLR:
                return  SendKeysAndWait(channel,Ascii.DC2,timeout,patern);
            case SpecialKeysConverter.CTRLS:
                return  SendKeysAndWait(channel,Ascii.DC3,timeout,patern);
            case SpecialKeysConverter.CTRLT:
                return  SendKeysAndWait(channel,Ascii.DC4,timeout,patern);
            case SpecialKeysConverter.CTRLU:
                return  SendKeysAndWait(channel,Ascii.NAK,timeout,patern);
            case SpecialKeysConverter.CTRLV:
                return  SendKeysAndWait(channel,Ascii.SYN,timeout,patern);
            case SpecialKeysConverter.CTRLW:
                return  SendKeysAndWait(channel,Ascii.ETB,timeout,patern);
            case SpecialKeysConverter.CTRLX:
                return  SendKeysAndWait(channel,Ascii.CAN,timeout,patern);
            case SpecialKeysConverter.CTRLY:
                return  SendKeysAndWait(channel,Ascii.EM,timeout,patern);
            case SpecialKeysConverter.CTRLZ:
                return  SendKeysAndWait(channel,Ascii.SUB,timeout,patern);
            default:
                return  SendNarrowKeysandWait(channel,Text.toUpperCase(),timeout,patern);

        }

    }
    public static StringBuilder SendSpecialKeys(TelnetClient channel,String Text,int timeout) throws IOException, RTETimeOutException {
        switch (Text.toUpperCase())
        {
            case SpecialKeysConverter.CR:
                return  SendKeys(channel,Ascii.CR,timeout);

            case SpecialKeysConverter.ESC:
                return  SendKeys(channel,Ascii.ESC,timeout);

            case SpecialKeysConverter.VT:
                return  SendKeys(channel,Ascii.VT,timeout);

            case SpecialKeysConverter.BS:
                return  SendKeys(channel,Ascii.BS,timeout);

            case SpecialKeysConverter.HT:
                return  SendKeys(channel,Ascii.HT,timeout);

            case SpecialKeysConverter.DEL:
                return  SendKeys(channel,Ascii.DEL,timeout);

            case SpecialKeysConverter.LF:
                return  SendKeys(channel,Ascii.LF,timeout);
            case SpecialKeysConverter.CTRLA:
                return  SendKeys(channel,Ascii.SOH,timeout);
            case SpecialKeysConverter.CTRLB:
                return  SendKeys(channel,Ascii.STX,timeout);
            case SpecialKeysConverter.CTRLC:
                return  SendKeys(channel,Ascii.ETX,timeout);
            case SpecialKeysConverter.CTRLD:
                return  SendKeys(channel,Ascii.EOT,timeout);
            case SpecialKeysConverter.CTRLE:
                return  SendKeys(channel,Ascii.ENQ,timeout);
            case SpecialKeysConverter.CTRLF:
                return  SendKeys(channel,Ascii.ACK,timeout);
            case SpecialKeysConverter.CTRLG:
                return  SendKeys(channel,Ascii.BEL,timeout);
            case SpecialKeysConverter.CTRLH:
                return  SendKeys(channel,Ascii.BS,timeout);
            case SpecialKeysConverter.CTRLI:
                return  SendKeys(channel,Ascii.HT,timeout);
            case SpecialKeysConverter.CTRLJ:
                return  SendKeys(channel,Ascii.LF,timeout);
            case SpecialKeysConverter.CTRLK:
                return  SendKeys(channel,Ascii.VT,timeout);
            case SpecialKeysConverter.CTRLL:
                return  SendKeys(channel,Ascii.FF,timeout);
            case SpecialKeysConverter.CTRLM:
                return  SendKeys(channel,Ascii.CR,timeout);
            case SpecialKeysConverter.CTRLN:
                return  SendKeys(channel,Ascii.SO,timeout);
            case SpecialKeysConverter.CTRLO:
                return  SendKeys(channel,Ascii.SI,timeout);
            case SpecialKeysConverter.CTRLP:
                return  SendKeys(channel,Ascii.DLE,timeout);
            case SpecialKeysConverter.CTRLQ:
                return  SendKeys(channel,Ascii.DC1,timeout);
            case SpecialKeysConverter.CTRLR:
                return  SendKeys(channel,Ascii.DC2,timeout);
            case SpecialKeysConverter.CTRLS:
                return  SendKeys(channel,Ascii.DC3,timeout);
            case SpecialKeysConverter.CTRLT:
                return  SendKeys(channel,Ascii.DC4,timeout);
            case SpecialKeysConverter.CTRLU:
                return  SendKeys(channel,Ascii.NAK,timeout);
            case SpecialKeysConverter.CTRLV:
                return  SendKeys(channel,Ascii.SYN,timeout);
            case SpecialKeysConverter.CTRLW:
                return  SendKeys(channel,Ascii.ETB,timeout);
            case SpecialKeysConverter.CTRLX:
                return  SendKeys(channel,Ascii.CAN,timeout);
            case SpecialKeysConverter.CTRLY:
                return  SendKeys(channel,Ascii.EM,timeout);
            case SpecialKeysConverter.CTRLZ:
                return  SendKeys(channel,Ascii.SUB,timeout);
            default:
                return  SendNarrowKeys(channel,Text.toUpperCase(),timeout);

        }

    }
    public static StringBuilder SendKeysAndWaitForPatern(TelnetClient channel,String Text,String patern,int timeout) throws IOException, RTETimeOutException {
        StringBuilder result;
        OutputStream out;
        InputStream in;

        out=channel.getOutputStream();
        in=channel.getInputStream();

        out.write(Text.getBytes());
        out.flush();

        result=readChannelOutput(channel,in,patern,timeout);

        return result;
    }
    public static void CloseSession(TelnetClient telnetClient) throws IOException {

        telnetClient.disconnect();


    }
}
