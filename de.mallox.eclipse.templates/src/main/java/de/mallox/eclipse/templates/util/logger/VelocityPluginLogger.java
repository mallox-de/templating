package de.mallox.eclipse.templates.util.logger;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

import de.mallox.eclipse.templates.Activator;

/**
 * {@link VelocityPluginLogger} use the plug in logger as logger for the
 * velocity engine.
 */
public class VelocityPluginLogger implements LogChute {

	/** Property key for specifying the name for the log instance */
	public static final String LOGCHUTE_COMMONS_LOG_NAME = "runtime.log.logsystem.commons.logging.name";

	/** Default name for the commons-logging instance */
	public static final String DEFAULT_LOG_NAME = "org.apache.velocity";

	private String name;

	@Override
	public void init(RuntimeServices pRuntimeServices) throws Exception {
		name = (String) pRuntimeServices.getProperty(LOGCHUTE_COMMONS_LOG_NAME);

		if (name == null) {
			name = DEFAULT_LOG_NAME;
		}
		log(LogChute.DEBUG_ID, "CommonsLogLogChute name is '" + name + "'");
	}

	@Override
	public void log(int pLevel, String pMessage) {
		switch (pLevel) {
		case LogChute.WARN_ID:
			Activator.logWarn(pMessage);
			break;
		case LogChute.INFO_ID:
			Activator.logInfo(pMessage);
			break;
		case LogChute.TRACE_ID:
			Activator.logInfo(pMessage);
			break;
		case LogChute.ERROR_ID:
			Activator.logError(pMessage);
			break;
		case LogChute.DEBUG_ID:
		default:
			Activator.logInfo(pMessage);
			break;
		}
	}

	@Override
	public void log(int pLevel, String pMessage, Throwable pThrowable) {
		switch (pLevel) {
		case LogChute.WARN_ID:
			Activator.logWarn(pMessage, pThrowable);
			break;
		case LogChute.INFO_ID:
			Activator.logInfo(pMessage, pThrowable);
			break;
		case LogChute.TRACE_ID:
			Activator.logInfo(pMessage, pThrowable);
			break;
		case LogChute.ERROR_ID:
			Activator.logError(pMessage, pThrowable);
			break;
		case LogChute.DEBUG_ID:
		default:
			Activator.logInfo(pMessage, pThrowable);
			break;
		}
	}

	@Override
	public boolean isLevelEnabled(int pLevel) {
		return pLevel > LogChute.INFO_ID;
	}

}
