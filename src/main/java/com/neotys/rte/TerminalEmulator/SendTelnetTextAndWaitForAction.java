package com.neotys.rte.TerminalEmulator;

import com.google.common.base.Optional;
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
public class SendTelnetTextAndWaitForAction implements com.neotys.extensions.action.Action {
    private static final String BUNDLE_NAME = "com.neotys.rte.TerminalEmulator.bundle";
    private static final String DISPLAY_NAME = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayNameSendTelnetKeyAndWaitFor");
    private static final String DISPLAY_PATH = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayPathTelnet");
    public static final String HOST = "HOST";
    public static final String TEXT = "TEXT";
    public static final String CHECK1="CHECK1";
    public static final String OPERATOR="OPERATOR";
    public static final String ClearBufferBefore="ClearBufferBefore";
    private static final ImageIcon LOGO_ICON;
    public static final String TimeOut = "TimeOut";

    @Override
    public String getType() {
        return "SendTelnetKeyAndWaitFor";
    }

    @Override
    public List<ActionParameter> getDefaultActionParameters() {
        final List<ActionParameter> parameters = new ArrayList<ActionParameter>();
        parameters.add(new ActionParameter(HOST, HOST));
        parameters.add(new ActionParameter(TEXT, "TEXT"));
        parameters.add(new ActionParameter(CHECK1, "CHECK"));
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
        return SendTelnetTextAndWaitForActionEngine.class;
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
        description.append("SendTelnetKeyAndWaitFor Will open the ssh connection to the remote Xterm Server.\n")
                .append("The parameters are : \n")
                .append("HOST  : host or ip of the server\n")
                .append("TEXT  : text that you would like to send \n")
                .append("CHECK1  :  Test to wait for \n")
                .append("CHECK2  :  2sd Test to wait for \n")
                .append("...CHECKx  :  X Test to wait for \n")
                .append("OPERATOR :  AND, OR. OPERATOR is required if you have more that one Check \n")
                .append("TimeOut  : max duration in seconds to open the ssh connection \n")
                .append("ClearBufferBefore  :  Optionnal  (default value : false), True: the action will clear the before sending keys \n");

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
