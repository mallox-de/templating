package de.mallox.eclipse.templates.widgets.preferences;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import de.mallox.eclipse.templates.preferences.TemplatePreferenceData;
import de.mallox.eclipse.templates.widgets.preferences.PreferenceTableViewer.ColumnName;

public class PreferenceCellModifier implements ICellModifier {

	private PreferenceTableViewer tableViewer;

	public PreferenceCellModifier(PreferenceTableViewer pPreferenceTableViewer) {
		tableViewer = pPreferenceTableViewer;
	}

	@Override
	public boolean canModify(Object pElement, String pProperty) {
		return true;
	}

	@Override
	public Object getValue(Object pElement, String pProperty) {

		if (pElement instanceof TemplatePreferenceData) {
			TemplatePreferenceData tTemplatePreferenceData = (TemplatePreferenceData) pElement;

			ColumnName tColumnName = PreferenceTableViewer.ColumnName.lookup(pProperty);

			switch (tColumnName) {
			case Name:
				return tTemplatePreferenceData.getName();
			case BaseUrl:
				return tTemplatePreferenceData.getBaseurl();
			case DescriptionFile:
				return tTemplatePreferenceData.getDescfile();
			default:
				break;
			}
		}
		return "";
	}

	@Override
	public void modify(Object pElement, String pProperty, Object pValue) {
		if (pElement instanceof TableItem) {
			TableItem tTableItem = (TableItem) pElement;

			if (tTableItem != null && tTableItem.getData() instanceof TemplatePreferenceData) {
				TemplatePreferenceData tTemplatePreferenceData = (TemplatePreferenceData) tTableItem.getData();

				ColumnName tColumnName = PreferenceTableViewer.ColumnName.lookup(pProperty);

				switch (tColumnName) {
				case Name:
					tTemplatePreferenceData.setName(String.valueOf(pValue));
					tableViewer.updateTask(tTemplatePreferenceData);
					break;
				case BaseUrl:
					/*
					 * On validation Error null will be passed. In this case we don't set the new value:
					 */
					if (pValue != null) {
						tTemplatePreferenceData.setBaseurl(String.valueOf(pValue));
					}
					tableViewer.updateTask(tTemplatePreferenceData);
					break;
				case DescriptionFile:
					tTemplatePreferenceData.setDescfile(String.valueOf(pValue));
					tableViewer.updateTask(tTemplatePreferenceData);
					break;
				default:
					break;
				}
			}
		}

	}

}
