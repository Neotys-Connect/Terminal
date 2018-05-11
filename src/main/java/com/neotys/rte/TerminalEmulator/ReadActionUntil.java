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
 * Created by hrexed on 11/05/18.
 */
public class ReadActionUntil implements Action {
    private static final String BUNDLE_NAME = "com.neotys.rte.TerminalEmulator.bundle";
    private static final String DISPLAY_NAME = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayNameReadActionUntil");
    private static final String DISPLAY_PATH = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayPath");
    public static final String HOST = "HOST";
    public static final String CHECK="CHECK";
    public static final String TimeOut = "TimeOut";
    private static final ImageIcon LOGO_ICON;


    @Override
    public String getType() {
        return "ReadActionUntil";
    }

    @Override
    public List<ActionParameter> getDefaultActionParameters() {
        final List<ActionParameter> parameters = new ArrayList<ActionParameter>();
        parameters.add(new ActionParameter(HOST, HOST));
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
        return ReadActionUntilEngine.class;
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
        description.append("ReadActionUntil Will open the ssh connection to the remote Xterm Server.\n")
                .append("The parameters are : \n")
                .append("HOST  : host or ip of the server\n")
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
