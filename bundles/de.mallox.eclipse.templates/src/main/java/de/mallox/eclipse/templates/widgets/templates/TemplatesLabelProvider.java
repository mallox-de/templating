package de.mallox.eclipse.templates.widgets.templates;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import de.mallox.eclipse.templates.model.TemplateIndexEntry;
import de.mallox.eclipse.templates.model.TemplateRefEntry;

public class TemplatesLabelProvider extends BaseLabelProvider implements ILabelProvider {

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof TemplateIndexEntry) {
			TemplateIndexEntry templateIndex = (TemplateIndexEntry) element;

			return templateIndex.getName();
		} else if (element instanceof TemplateRefEntry) {
			TemplateRefEntry template = (TemplateRefEntry) element;

			return template.getName();

		}
		return "";
	}

}
