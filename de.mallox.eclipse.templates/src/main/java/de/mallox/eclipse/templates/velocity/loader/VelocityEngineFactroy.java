package de.mallox.eclipse.templates.velocity.loader;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.apache.velocity.runtime.resource.loader.URLResourceLoader;

import de.mallox.eclipse.templates.util.logger.VelocityPluginLogger;

public class VelocityEngineFactroy {

	public static VelocityEngine create(String pBasePath) {
		String tScheme = getScheme(pBasePath);
		VelocityEngine tVelocityEngine = new VelocityEngine();
		if ("http".equals(tScheme) || "https".equals(tScheme)) {
			tVelocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "url");
			tVelocityEngine.setProperty("url." + RuntimeConstants.RESOURCE_LOADER + ".class",
					HttpResourceLoader.class.getName());
			tVelocityEngine.setProperty("url." + RuntimeConstants.RESOURCE_LOADER + ".root", pBasePath);
		} else if ("file".equals(tScheme) || "ftp".equals(tScheme)) {
			tVelocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "url");
			tVelocityEngine.setProperty("url." + RuntimeConstants.RESOURCE_LOADER + ".class",
					URLResourceLoader.class.getName());
			tVelocityEngine.setProperty("url." + RuntimeConstants.RESOURCE_LOADER + ".root", pBasePath);
		} else {
			tVelocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
			tVelocityEngine.setProperty("file." + RuntimeConstants.RESOURCE_LOADER + ".class",
					FileResourceLoader.class.getName());
			tVelocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, pBasePath);
		}
		tVelocityEngine.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, VelocityPluginLogger.class.getName());

		return tVelocityEngine;
	}

	/**
	 * Determine the scheme of the given URL; e.g. http or file.
	 * 
	 * @param pURL
	 * @return scheme of the given URL
	 */
	public static String getScheme(String pURL) {
		try {
			String tScheme = new URI(pURL).getScheme();
			return tScheme.toLowerCase();
		} catch (URISyntaxException e) {
			return null;
		}
	}

}
