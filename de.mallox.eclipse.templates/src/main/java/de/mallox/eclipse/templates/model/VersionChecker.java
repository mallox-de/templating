package de.mallox.eclipse.templates.model;

import org.osgi.framework.Version;

import de.mallox.eclipse.templates.Activator;

public class VersionChecker {
	/**
	 * Check if the plugin is compatible with the template to perform.
	 * 
	 * @param pGeneratorVersion
	 *            {@link String} min version of the generator, which can be
	 *            used.
	 * @return <code>true</code>, if this template fits to this plugin,
	 *         otherwise <code>false</code> will be returned.
	 */
	public static boolean checkCompatibilityOfPlugin(String pGeneratorVersion) {
		if (pGeneratorVersion != null) {
			Version tGeneratorVersion = new Version(pGeneratorVersion);
			Version pCurrentVersion = Activator.getVersion();

			if (pCurrentVersion.getMajor() == tGeneratorVersion.getMajor()
					&& pCurrentVersion.getMinor() >= tGeneratorVersion.getMinor()
					&& pCurrentVersion.getMicro() >= tGeneratorVersion.getMicro()) {

				// Version fits:
				return true;
			}
		}

		return false;
	}

}
