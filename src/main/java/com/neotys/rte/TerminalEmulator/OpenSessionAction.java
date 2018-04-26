package com.neotys.rte.TerminalEmulator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.*;

import com.google.common.base.Optional;
import com.neotys.extensions.action.Action;
import com.neotys.extensions.action.ActionParameter;
import com.neotys.extensions.action.engine.ActionEngine;

public final class OpenSessionAction implements Action{
	private static final String BUNDLE_NAME = "com.neotys.rte.TerminalEmulator.bundle";
	private static final String DISPLAY_NAME = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayNameOpenSession");
	private static final String DISPLAY_PATH = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault()).getString("displayPath");
	public static final String HOST="HOST";
	public static final String Port="Port";
	public static final String UserName="UserName";
	public static final String Password="Password";
	public static final String TimeOut="TimeOut";
	private static final ImageIcon LOGO_ICON;


	@Override
	public String getType() {
		return "OpenSession";
	}

	@Override
	public List<ActionParameter> getDefaultActionParameters() {
		final List<ActionParameter> parameters = new ArrayList<ActionParameter>();
		parameters.add(new ActionParameter(HOST,HOST));
		parameters.add(new ActionParameter(Port,"22"));
		parameters.add(new ActionParameter(UserName,"test"));
		parameters.add(new ActionParameter(Password,"Password"));
		parameters.add(new ActionParameter(TimeOut,"30"));
		// TODO Add default parameters.
		return parameters;
	}
	static {
		final URL iconURL = OpenSessionAction.class.getResource("logo.png");
		if (iconURL != null) {
			LOGO_ICON = new ImageIcon(iconURL);
		}
		else {
			LOGO_ICON = null;
		}
	}
	@Override
	public Class<? extends ActionEngine> getEngineClass() {
		return OpenSessionActionEngine.class;
	}

	@Override
	public Icon getIcon() {
		// TODO Add an icon
		return LOGO_ICON;
	}

	@Override
	public boolean getDefaultIsHit(){
		return true;
	}

	@Override
	public String getDescription() {
		final StringBuilder description = new StringBuilder();
		// TODO Add description
		description.append("CreateSession Will open the ssh connection to the remote Xterm Server.\n")
				.append("The parameters are : \n")
				.append("HOST  : host or ip of the server\n")
				.append("Port  : ssh port \n")
				.append("UserName  : Username to open the ssh connection \n")
				.append("Password  : ssh password \n")
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
