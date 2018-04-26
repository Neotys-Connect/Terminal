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
    public static String[] SpecialKeys={CR,ESC,DEL,BS,HT,LF,VT};
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

        }
        return null;
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

        }
        return null;
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
