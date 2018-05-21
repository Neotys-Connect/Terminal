package com.neotys.rte.TerminalEmulator;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.List;
import java.util.Properties;
import com.google.common.base.Ascii;


/**
 * Created by hrexed on 20/03/18.
 */
public class TerminalUtils {
    static Session session;
    private static OutputStream out;
    private static InputStream in;
    private final static int MAX_THREAD_SLEEP=200;
    private final static String CR="CR";
    private final static String ESC="ESC";
    private final static String DEL="DEL";
    private final static String BS="BS";
    private final static String HT="HT";
    private final static String LF="LF";
    private final static String VT="VT";
    private final static String CTRLA="CTRLA";
    private final static String CTRLB="CTRLB";
    private final static String CTRLC="CTRLC";
    private final static String CTRLD="CTRLD";
    private final static String CTRLE="CTRLE";
    private final static String CTRLF="CTRLF";
    private final static String CTRLG="CTRLG";
    private final static String CTRLH="CTRLH";
    private final static String CTRLI="CTRLI";
    private final static String CTRLJ="CTRLJ";
    private final static String CTRLK="CTRLK";
    private final static String CTRLL="CTRLL";
    private final static String CTRLM="CTRLM";
    private final static String CTRLN="CTRLN";
    private final static String CTRLO="CTRLO";
    private final static String CTRLP="CTRLP";
    private final static String CTRLQ="CTRLQ";
    private final static String CTRLR="CTRLR";
    private final static String CTRLS="CTRLS";
    private final static String CTRLT="CTRLT";
    private final static String CTRLU="CTRLU";
    private final static String CTRLV="CTRLV";
    private final static String CTRLW="CTRLW";
    private final static String CTRLX="CTRLX";
    private final static String CTRLY="CTRLY";
    private final static String CTRLZ="CTRLZ";
    private final static String UP="UP";
    private final static String DOWN="DOWN";
    private final static String LEFT="LEFT";
    private final static String RIGHT="RIGHT";

    public static String[] SpecialKeys={CR,ESC,DEL,BS,HT,LF,VT,CTRLA,CTRLB,CTRLC,CTRLD,CTRLE,CTRLF,CTRLG,CTRLH,CTRLI,CTRLJ,CTRLH,CTRLM,CTRLN,CTRLO,CTRLP,CTRLQ,CTRLR,CTRLS,CTRLT,CTRLU,CTRLV,CTRLW,CTRLX,CTRLY,CTRLZ,UP,DOWN,LEFT,RIGHT};
    static Channel channel;



    public static boolean IsKeyInTheList(String Key)
    {
        for(int i=0;i<SpecialKeys.length;i++)
        {
            if(SpecialKeys[i].equalsIgnoreCase(Key))
                return true;
        }
        return false;
    }
    private static String CleanOutput(String line)
    {
        line=line.replaceAll("\u001B\\[[\\d;]*[^\\d;]","");
        line=line.replaceAll("1h\u001B=","");
        line=line.replaceAll("\u001B\\(B","");
        line=line.replaceAll("\u001B\\)0","");
        line=line.replaceAll("\u000F7h","");
        line=line.replaceAll("\u000F","");

        return line;
    }
    private static StringBuilder  readChannelOutput(Channel channel,InputStream in,int timeout) throws IOException {
        byte[] buffer = new byte[1024];
        StringBuilder result= new StringBuilder();
        long t= System.currentTimeMillis();
        long end = t+(timeout*1000);

        String line = "";
        while(System.currentTimeMillis() < end)
        {
            while (in.available() > 0)
            {
                int i = in.read(buffer, 0, 1024);
                if (i < 0) {
                    break;
                }
                line = new String(buffer, 0, i);
                line=CleanOutput(line);
                result.append(line+"\n");


            }

            if (channel.isClosed()){
                break;
            }
            try {
                Thread.sleep(MAX_THREAD_SLEEP);
            } catch (Exception ee){
                return result;
            }
        }

        return result;
    }
    public static StringBuilder ReadUntil(Channel channel,String Check,int timeout) throws IOException {
        StringBuilder result;
        OutputStream out;
        InputStream in;

        in=channel.getInputStream();
        result= readChannelOutput(channel,in,Check,timeout);

        return result;
    }
    private static StringBuilder  readChannelOutput(Channel channel,InputStream in,String Check,int timeout) throws IOException {
        byte[] buffer = new byte[1024];
        StringBuilder result= new StringBuilder();
        long t= System.currentTimeMillis();
        long end = t+(timeout*1000);

        String line = "";
        while(System.currentTimeMillis() < end)
        {
            while (in.available() > 0)
            {
                int i = in.read(buffer, 0, 1024);
                if (i < 0) {
                    break;
                }
                line = new String(buffer, 0, i);
                line=CleanOutput(line);
                result.append(line+"\n");

                if(line.contains(Check)){
                    break;
                }
            }


            if(line.contains(Check)){
                break;
            }

            if (channel.isClosed()){
                break;
            }


            try {
                Thread.sleep(MAX_THREAD_SLEEP);
            } catch (Exception ee){
                return result;
            }
        }

        return result;
    }

    public static boolean IsPaternInStringbuilder(String Check,StringBuilder content)
    {
        if(content.toString().contains(Check))
            return true;
        else
            return false;
    }

    public static StringBuilder SendKeysAndWait(Channel channel,byte Text,int timeout,String patern) throws IOException
    {
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

    public static StringBuilder SendKeys(Channel channel,byte Text,int timeout) throws IOException
    {
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
    public static StringBuilder SendKeys(Channel channel,String Text,int timeout) throws IOException
    {
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
    public static StringBuilder SendNarrowKeys(Channel channel,String Text,int timeout) throws IOException
    {
        StringBuilder result;
        OutputStream out;
        InputStream in;

        out=channel.getOutputStream();
        in=channel.getInputStream();

        out.write(Ascii.ESC);

        switch (Text)
        {
            case UP:
                out.write("[A".getBytes());

                break;
            case DOWN:
                out.write("[B".getBytes());

                break;
            case LEFT:
                out.write("[D".getBytes());

                break;
            case RIGHT:
                out.write("[C".getBytes());

                break;
        }
        out.flush();

        result=readChannelOutput(channel,in,timeout);

        return result;

    }
    public static StringBuilder SendNarrowKeysandWait(Channel channel,String Text,int timeout,String patern) throws IOException
    {
        StringBuilder result;
        OutputStream out;
        InputStream in;

        out=channel.getOutputStream();
        in=channel.getInputStream();

        out.write(Ascii.ESC);

        switch (Text)
        {
            case UP:
                out.write("[A".getBytes());

                break;
            case DOWN:
                out.write("[B".getBytes());

                break;
            case LEFT:
                out.write("[D".getBytes());

                break;
            case RIGHT:
                out.write("[C".getBytes());

                break;
        }
        out.flush();

        result=readChannelOutput(channel,in,patern,timeout);


        return result;

    }


    public static StringBuilder SendSpecialKeysAndWaitFor(Channel channel,String Text,int timeout,String patern) throws IOException {
        switch (Text.toUpperCase())
        {
            case CR:
                return  SendKeysAndWait(channel,Ascii.CR,timeout,patern);

            case ESC:
                return  SendKeysAndWait(channel,Ascii.ESC,timeout,patern);

            case VT:
                return  SendKeysAndWait(channel,Ascii.VT,timeout,patern);

            case BS:
                return  SendKeysAndWait(channel,Ascii.BS,timeout,patern);

            case HT:
                return  SendKeysAndWait(channel,Ascii.HT,timeout,patern);

            case DEL:
                return  SendKeysAndWait(channel,Ascii.DEL,timeout,patern);

            case LF:
                return  SendKeysAndWait(channel,Ascii.LF,timeout,patern);
            case CTRLA:
                return  SendKeysAndWait(channel,Ascii.SOH,timeout,patern);
            case CTRLB:
                return  SendKeysAndWait(channel,Ascii.STX,timeout,patern);
            case CTRLC:
                return  SendKeysAndWait(channel,Ascii.ETX,timeout,patern);
            case CTRLD:
                return  SendKeysAndWait(channel,Ascii.EOT,timeout,patern);
            case CTRLE:
                return  SendKeysAndWait(channel,Ascii.ENQ,timeout,patern);
            case CTRLF:
                return  SendKeysAndWait(channel,Ascii.ACK,timeout,patern);
            case CTRLG:
                return  SendKeysAndWait(channel,Ascii.BEL,timeout,patern);
            case CTRLH:
                return  SendKeysAndWait(channel,Ascii.BS,timeout,patern);
            case CTRLI:
                return  SendKeysAndWait(channel,Ascii.HT,timeout,patern);
            case CTRLJ:
                return  SendKeysAndWait(channel,Ascii.LF,timeout,patern);
            case CTRLK:
                return  SendKeysAndWait(channel,Ascii.VT,timeout,patern);
            case CTRLL:
                return  SendKeysAndWait(channel,Ascii.FF,timeout,patern);
            case CTRLM:
                return  SendKeysAndWait(channel,Ascii.CR,timeout,patern);
            case CTRLN:
                return  SendKeysAndWait(channel,Ascii.SO,timeout,patern);
            case CTRLO:
                return  SendKeysAndWait(channel,Ascii.SI,timeout,patern);
            case CTRLP:
                return  SendKeysAndWait(channel,Ascii.DLE,timeout,patern);
            case CTRLQ:
                return  SendKeysAndWait(channel,Ascii.DC1,timeout,patern);
            case CTRLR:
                return  SendKeysAndWait(channel,Ascii.DC2,timeout,patern);
            case CTRLS:
                return  SendKeysAndWait(channel,Ascii.DC3,timeout,patern);
            case CTRLT:
                return  SendKeysAndWait(channel,Ascii.DC4,timeout,patern);
            case CTRLU:
                return  SendKeysAndWait(channel,Ascii.NAK,timeout,patern);
            case CTRLV:
                return  SendKeysAndWait(channel,Ascii.SYN,timeout,patern);
            case CTRLW:
                return  SendKeysAndWait(channel,Ascii.ETB,timeout,patern);
            case CTRLX:
                return  SendKeysAndWait(channel,Ascii.CAN,timeout,patern);
            case CTRLY:
                return  SendKeysAndWait(channel,Ascii.EM,timeout,patern);
            case CTRLZ:
                return  SendKeysAndWait(channel,Ascii.SUB,timeout,patern);
           default:
                return  SendNarrowKeysandWait(channel,Text.toUpperCase(),timeout,patern);

        }

    }
    public static StringBuilder SendSpecialKeys(Channel channel,String Text,int timeout) throws IOException {
        switch (Text.toUpperCase())
        {
            case CR:
                return  SendKeys(channel,Ascii.CR,timeout);

            case ESC:
                return  SendKeys(channel,Ascii.ESC,timeout);

            case VT:
                return  SendKeys(channel,Ascii.VT,timeout);

            case BS:
                return  SendKeys(channel,Ascii.BS,timeout);

            case HT:
                return  SendKeys(channel,Ascii.HT,timeout);

            case DEL:
                return  SendKeys(channel,Ascii.DEL,timeout);

            case LF:
                return  SendKeys(channel,Ascii.LF,timeout);
            case CTRLA:
                return  SendKeys(channel,Ascii.SOH,timeout);
            case CTRLB:
                return  SendKeys(channel,Ascii.STX,timeout);
            case CTRLC:
                return  SendKeys(channel,Ascii.ETX,timeout);
            case CTRLD:
                return  SendKeys(channel,Ascii.EOT,timeout);
            case CTRLE:
                return  SendKeys(channel,Ascii.ENQ,timeout);
            case CTRLF:
                return  SendKeys(channel,Ascii.ACK,timeout);
            case CTRLG:
                return  SendKeys(channel,Ascii.BEL,timeout);
            case CTRLH:
                return  SendKeys(channel,Ascii.BS,timeout);
            case CTRLI:
                return  SendKeys(channel,Ascii.HT,timeout);
            case CTRLJ:
                return  SendKeys(channel,Ascii.LF,timeout);
            case CTRLK:
                return  SendKeys(channel,Ascii.VT,timeout);
            case CTRLL:
                return  SendKeys(channel,Ascii.FF,timeout);
            case CTRLM:
                return  SendKeys(channel,Ascii.CR,timeout);
            case CTRLN:
                return  SendKeys(channel,Ascii.SO,timeout);
            case CTRLO:
                return  SendKeys(channel,Ascii.SI,timeout);
            case CTRLP:
                return  SendKeys(channel,Ascii.DLE,timeout);
            case CTRLQ:
                return  SendKeys(channel,Ascii.DC1,timeout);
            case CTRLR:
                return  SendKeys(channel,Ascii.DC2,timeout);
            case CTRLS:
                return  SendKeys(channel,Ascii.DC3,timeout);
            case CTRLT:
                return  SendKeys(channel,Ascii.DC4,timeout);
            case CTRLU:
                return  SendKeys(channel,Ascii.NAK,timeout);
            case CTRLV:
                return  SendKeys(channel,Ascii.SYN,timeout);
            case CTRLW:
                return  SendKeys(channel,Ascii.ETB,timeout);
            case CTRLX:
                return  SendKeys(channel,Ascii.CAN,timeout);
            case CTRLY:
                return  SendKeys(channel,Ascii.EM,timeout);
            case CTRLZ:
                return  SendKeys(channel,Ascii.SUB,timeout);
            default:
                return  SendNarrowKeys(channel,Text.toUpperCase(),timeout);

        }

    }
    public static StringBuilder SendKeysAndWaitForPatern(Channel channel,String Text,String patern,int timeout) throws IOException {
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

    public static void CloseSession(Channel channel) throws JSchException {
        Session sess=channel.getSession();
        channel.disconnect();
        sess.disconnect();
    }
    public static Channel OpenSession(String Host,int port, String Username, String password,int timeout) throws JSchException {
        JSch jsch=new JSch();


        timeout=timeout*1000;

        session=jsch.getSession(Username, Host, port);
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);


        session.setPassword(password);
        session.connect(timeout);

        channel=session.openChannel("shell");
        ((ChannelShell)channel).setPtyType("dumb");
        ((ChannelShell)channel).setPty(false);

        channel.connect();

        return channel;
    }



}
