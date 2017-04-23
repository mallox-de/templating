package de.mallox.eclipse.templates;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.internal.net.auth.Authentication;
import org.eclipse.ui.internal.net.auth.UserValidationDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.mallox.eclipse.templates"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private Map<String, Credentials> authenticationMap;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext pContext) throws Exception {
		authenticationMap = new HashMap<String, Credentials>();
		super.start(pContext);
		plugin = this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext pContext) throws Exception {
		plugin = null;
		super.stop(pContext);
		authenticationMap.clear();
		authenticationMap = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	// TODO extract to own class ...
	public synchronized Credentials getAuthentication(String pName) {
		Credentials tCredentials = authenticationMap.get(pName.toLowerCase());

		System.out.println("\"" + pName.toLowerCase() + "\"");

		if (tCredentials == null) {
			synchronized (authenticationMap) {
				tCredentials = authenticationMap.get(pName.toLowerCase());

				if (tCredentials == null) {
					Authentication tAuthentication = UserValidationDialog.getAuthentication(pName, "Login necessary:");
					if (tAuthentication == null) {
						return null;
					}

					tCredentials = new UsernamePasswordCredentials(tAuthentication.getUser(),
							tAuthentication.getPassword());

					authenticationMap.put(pName.toLowerCase(), tCredentials);
				}

			}
		}
		return tCredentials;
	}

	public void removeAuthentication(String pName) {
		authenticationMap.remove(pName.toLowerCase());
	}

	public void removeAllAuthentications() {
		authenticationMap.clear();
	}

	public static Version getVersion() {
		Bundle tBundle = Platform.getBundle(PLUGIN_ID);
		return tBundle.getVersion();
	}

	public static void logInfo(String pMessage) {
		ILog tLog = getDefault().getLog();

		tLog.log(new Status(IStatus.INFO, PLUGIN_ID, pMessage));
	}

	public static void logInfo(String pMessage, Throwable pThrowable) {
		ILog tLog = getDefault().getLog();

		tLog.log(new Status(IStatus.INFO, PLUGIN_ID, pMessage, pThrowable));
	}

	public static void logWarn(String pMessage) {
		ILog tLog = getDefault().getLog();

		tLog.log(new Status(IStatus.WARNING, PLUGIN_ID, pMessage));
	}

	public static void logWarn(String pMessage, Throwable pThrowable) {
		ILog tLog = getDefault().getLog();

		tLog.log(new Status(IStatus.WARNING, PLUGIN_ID, pMessage, pThrowable));
	}

	public static void logError(String pMessage) {
		ILog tLog = getDefault().getLog();

		tLog.log(new Status(IStatus.ERROR, PLUGIN_ID, pMessage));
	}

	public static void logError(String pMessage, Throwable pThrowable) {
		ILog tLog = getDefault().getLog();

		tLog.log(new Status(IStatus.ERROR, PLUGIN_ID, pMessage, pThrowable));
	}
}
