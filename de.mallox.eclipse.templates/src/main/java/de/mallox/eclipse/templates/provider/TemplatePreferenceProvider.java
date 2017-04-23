package de.mallox.eclipse.templates.provider;

import org.eclipse.jface.preference.IPreferenceStore;

import de.mallox.eclipse.templates.Activator;
import de.mallox.eclipse.templates.preferences.TemplatePreferenceData;
import de.mallox.eclipse.templates.preferences.TemplatePreferenceDataList;

public class TemplatePreferenceProvider {
	public TemplatePreferenceDataList readPreference() {
		TemplatePreferenceDataList tPrefDataList = new TemplatePreferenceDataList();

		IPreferenceStore tIPreferenceStore = Activator.getDefault().getPreferenceStore();

		for (int i = 0;; i++) {

			String tEntryPrefix = TemplatePreferenceData.TEMPLATE_PREFIX + i;

			if (!tIPreferenceStore.contains(tEntryPrefix + TemplatePreferenceData.NAME_POSTFIX)) {
				// no more elements
				break;
			}

			String name = tIPreferenceStore.getString(tEntryPrefix + TemplatePreferenceData.NAME_POSTFIX);
			String baseurl = tIPreferenceStore.getString(tEntryPrefix + TemplatePreferenceData.BASEURL_POSTFIX);
			String descfile = tIPreferenceStore.getString(tEntryPrefix + TemplatePreferenceData.DESCFILE_POSTFIX);

			tPrefDataList.addTask(new TemplatePreferenceData(name, baseurl, descfile));
		}
		return tPrefDataList;
	}
	
	/**
	 * Saves the preferences to the PreferencesStore.
	 *
	 * @param table
	 *            [in] table where values are displayed
	 *
	 * @.author matysiak
	 *
	 * @.threadsafe no
	 *
	 *              <!-- add optional tags @version, @see, @since, @deprecated
	 *              here -->
	 */
	public void saveTableToPrefs(TemplatePreferenceDataList pPrefDataList) {
		int i = 0;
		IPreferenceStore tIPreferenceStore = Activator.getDefault().getPreferenceStore();

		// store data from file table rows
		for (TemplatePreferenceData tPrefData : pPrefDataList.getTemplatePreferenceDataList()) {
			String tEntryPrefix = TemplatePreferenceData.TEMPLATE_PREFIX + i;

			tIPreferenceStore.setValue(tEntryPrefix + TemplatePreferenceData.NAME_POSTFIX, tPrefData.getName());
			tIPreferenceStore.setValue(tEntryPrefix + TemplatePreferenceData.BASEURL_POSTFIX, tPrefData.getBaseurl());
			tIPreferenceStore.setValue(tEntryPrefix + TemplatePreferenceData.DESCFILE_POSTFIX, tPrefData.getDescfile());
			i++;
		}

		// remove items that have been deleted
		for (;; i++) {

			String tEntryPrefix = TemplatePreferenceData.TEMPLATE_PREFIX + i;

			if (!tIPreferenceStore.contains(tEntryPrefix + TemplatePreferenceData.NAME_POSTFIX)) {
				// no more elements
				break;
			}

			tIPreferenceStore.setValue(tEntryPrefix + TemplatePreferenceData.NAME_POSTFIX, "");
			tIPreferenceStore.setValue(tEntryPrefix + TemplatePreferenceData.BASEURL_POSTFIX, "");
			tIPreferenceStore.setValue(tEntryPrefix + TemplatePreferenceData.DESCFILE_POSTFIX, "");

		}
	}
	
}
