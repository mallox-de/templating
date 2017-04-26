package de.mallox.eclipse.templates.widgets.preferences;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.mallox.eclipse.templates.preferences.TemplatePreferenceData;

public class PreferenceLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object pElement, int pColumnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object pElement, int pColumnIndex) {
		if (pElement instanceof TemplatePreferenceData) {
			TemplatePreferenceData tTemplatePreferenceData = (TemplatePreferenceData) pElement;
			
			switch (pColumnIndex) {
			case 0:
				return tTemplatePreferenceData.getName();
			case 1:
				return tTemplatePreferenceData.getBaseurl();
			case 2:
				return tTemplatePreferenceData.getDescfile();
			}
			
			return "";
		}
		return null;
	}

}
