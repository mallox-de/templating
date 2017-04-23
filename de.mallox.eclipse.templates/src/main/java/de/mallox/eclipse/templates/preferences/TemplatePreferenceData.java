package de.mallox.eclipse.templates.preferences;

import de.mallox.eclipse.templates.Activator;

public class TemplatePreferenceData {

	/** prefix for template preference */
	public static final String TEMPLATE_PREFIX = Activator.PLUGIN_ID + ".template";

	/** suffix for name preference */
	public static final String NAME_POSTFIX = ".name";

	/** suffix for base URL preference */
	public static final String BASEURL_POSTFIX = ".baseurl";

	/** suffix for description file preference */
	public static final String DESCFILE_POSTFIX = ".descfile";

	private String name;

	private String baseurl;

	private String descfile;

	public TemplatePreferenceData() {
		super();
	}

	public TemplatePreferenceData(String pName, String pBaseurl, String pDescfile) {
		name = pName;
		baseurl = pBaseurl;
		descfile = pDescfile;
	}

	public String getName() {
		return name;
	}

	public void setName(String pName) {
		name = pName;
	}

	public String getBaseurl() {
		return baseurl;
	}

	public void setBaseurl(String pBaseurl) {
		baseurl = pBaseurl;
	}

	public String getDescfile() {
		return descfile;
	}

	public void setDescfile(String pDescfile) {
		descfile = pDescfile;
	}
}
