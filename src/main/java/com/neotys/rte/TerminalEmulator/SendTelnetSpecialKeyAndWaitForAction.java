package com.neotys.rte.TerminalEmulator;

import com.google.common.base.Optional;
import com.neotys.extensions.action.Action;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;

import javax.swing.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by hrexed on 26/04/18.
 */
public class SendTelnetSpecialKeyAndWaitForAction implements Action {
    private static final String BUNDLE_NAME = "com.neotys.rte.TerminalEmulator.bundle";
    private static final String DISPLAY_NAME = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayNameSendTelnetSpecialKeyAndWaitFor");
    private static final String DISPLAY_PATH = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayPathTelnet");
    public static final String HOST = "HOST";
    public static final String KEY="KEY";
    public static final String CHECK="CHECK";
    public static final String TimeOut = "TimeOut";
    private static final ImageIcon LOGO_ICON;


    @Override
    public String getType() {
        return "SendTelnetSpecialKeyAndWaitFor";
    }

    @Override
    public List<ActionParameter> getDefaultActionParameters() {
        final List<ActionParameter> parameters = new ArrayList<ActionParameter>();
        parameters.add(new ActionParameter(HOST, HOST));
        parameters.add(new ActionParameter(KEY, "CR"));
        parameters.add(new ActionParameter(CHECK, "CHECK"));
        parameters.add(new ActionParameter(TimeOut, "5"));

        // TODO Add default parameters.
        return parameters;
    }

    static {
        final URL iconURL = OpenSessionAction.class.getResource("logo.png");
        if (iconURL != null) {
            LOGO_ICON = new ImageIcon(iconURL);
        } else {
            LOGO_ICON = null;
        }
    }

    @Override
    public Class<? extends ActionEngine> getEngineClass() {
        return SendTelnetSpecialKeyActionEngine.class;
    }

    @Override
    public Icon getIcon() {
        // TODO Add an icon
        return LOGO_ICON;
    }

    @Override
    public boolean getDefaultIsHit() {
        return true;
    }

    @Override
    public String getDescription() {
        final StringBuilder description = new StringBuilder();
        // TODO Add description
        description.append("SendTelnetSpecialKeyAndWaitFor Will open the ssh connection to the remote Xterm Server.\n")
                .append("The parameters are : \n")
                .append("HOST  : host or ip of the server\n")
                .append("KEY  :  Send special KeyBoard keys : \n")
                .append("\t\tCR : Carriage return  \n")
                .append("\t\tBS : Backspace \n")
                .append("\t\tHT : Horizontal TAB  \n")
                .append("\t\tLF : Linefeed (newline) \n")
                .append("\t\tVT : Vertical TAB \n")
                .append("\t\tESC : Escape character \n")
                .append("\t\tDEL : Delete character \n")
                .append("\t\tCTRLA : CTRL A \n")
                .append("\t\tCTRLB : CTRL A \n")
                .append("\t\t.......\n")
                .append("\t\tCTRLZ : CTRL Z \n")
                .append("\t\tUP : up narrow \n")
                .append("\t\tDOWN : down narrow \n")
                .append("\t\tRIGHT : rigth narrow \n")
                .append("\t\tLEFT : left narrow \n")
                .append("Check  :  Test to wait for \n")
                .append("TimeOut  : max duration in seconds to open the ssh connection \n");
        return description.toString();
    }

    @Override
    public String getDisplayName() {
        return DISPLAY_NAME;
    }

    @Override
    public String getDisplayPath() {
        return DISPLAY_PATH;
    }

    @Override
    public Optional<String> getMinimumNeoLoadVersion() {
        return Optional.of("6.2");
    }

    @Override
    public Optional<String> getMaximumNeoLoadVersion() {
        return Optional.absent();
    }
}

