/**
 * 
 */
package de.mallox.eclipse.templates.widgets.variables;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.mallox.eclipse.templates.model.TemplateEntry;

/**
 * @author ted
 *
 */
public class VariablesContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
		// NOP
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// NOP
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof TemplateEntry) {
			TemplateEntry templateEntry = (TemplateEntry) inputElement;

			return templateEntry.getVariable().toArray();
		}
		return null;
	}

}
