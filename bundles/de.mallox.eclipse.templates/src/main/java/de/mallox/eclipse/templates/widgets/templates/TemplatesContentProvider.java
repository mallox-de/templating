package de.mallox.eclipse.templates.widgets.templates;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.mallox.eclipse.templates.model.TemplateIndexEntry;
import de.mallox.eclipse.templates.model.TemplateRefEntry;

// http://www.eclipse.org/articles/Article-TreeViewer/TreeViewerArticle.htm?PHPSESSID=4d48764999a9cb66a7fd58a954ef2131
public class TemplatesContentProvider implements ITreeContentProvider {

	private static final Object[] EMPTY_ARRAY = new Object[] {};

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// NOP
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof List) {
			/*
			 * Liste mit TemplateIndex:
			 */
			@SuppressWarnings("unchecked")
			List<TemplateIndexEntry> templateIndexs = (List<TemplateIndexEntry>) parentElement;
			return templateIndexs.toArray();
		} else if (parentElement instanceof TemplateIndexEntry) {
			/*
			 * TemplateIndex:
			 */
			TemplateIndexEntry templateIndex = (TemplateIndexEntry) parentElement;

			List<TemplateRefEntry> templates = templateIndex.getTemplateRef();

			return templates.toArray();
		}
		/*
		 * Template:
		 */
		return EMPTY_ARRAY;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

}
