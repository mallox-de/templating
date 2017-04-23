/**
 * 
 */
package de.mallox.eclipse.templates.widgets.preferences;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.mallox.eclipse.templates.preferences.TemplatePreferenceDataList;

/**
 *
 */
public class PreferenceContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
		// NOP
	}

	@Override
	public void inputChanged(Viewer pViewer, Object oldInput, Object pNewInput) {
		// NOP
	}

	@Override
	public Object[] getElements(Object pInputElement) {

		if (pInputElement instanceof TemplatePreferenceDataList) {
			TemplatePreferenceDataList templatePreferenceDataList = (TemplatePreferenceDataList) pInputElement;
			
			return templatePreferenceDataList.getTemplatePreferenceDataList().toArray();
		}
		return null;
	}

}
