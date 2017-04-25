package de.mallox.eclipse.templates.wizards.create;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import org.eclipse.jdt.core.IJavaElement;

/**
 * Filters out all packages, folders, <code>.project</code> and
 * <code>.classpath</code>.
 */
class ContainerFilter extends ViewerFilter {

	private boolean filterContainers;

	public static boolean FILTER_CONTAINERS = true;
	public static boolean FILTER_NON_CONTAINERS = false;

	public ContainerFilter(boolean pFilterContainers) {
		filterContainers = pFilterContainers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean select(Viewer pViewer, Object pParent, Object pElement) {
		boolean tIsContainer = pElement instanceof IContainer;
		if (!tIsContainer) {
			if (pElement instanceof IJavaElement) {
				int tType = ((IJavaElement) pElement).getElementType();
				tIsContainer = tType == IJavaElement.JAVA_MODEL || tType == IJavaElement.JAVA_PROJECT
						|| tType == IJavaElement.PACKAGE_FRAGMENT || tType == IJavaElement.PACKAGE_FRAGMENT_ROOT;
			} else if (pElement instanceof IProject) {
				IProject tIProject = (IProject) pElement;
				
				int tType = tIProject.getType();
				
				tIsContainer = tType == IProject.PROJECT;				
			}
		}
		if (!tIsContainer && pElement instanceof IFile) {
			IFile tFile = (IFile) pElement;

			if (".project".equals(tFile.getName())) {
				return false;
			} else if (".classpath".equals(tFile.getName())) {
				return false;
			}

		}
		return (filterContainers && !tIsContainer) || (!filterContainers && tIsContainer);
	}
}
