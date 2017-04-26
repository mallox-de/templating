package de.mallox.eclipse.templates.util;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class WorkspaceUtility {
	public static void createParentFolder(IResource pResource, IProgressMonitor pMonitor) throws CoreException {
		if (pResource instanceof IFolder) {
			IFolder tIFolder = (IFolder) pResource;
			if (!tIFolder.exists()) {
				createParentFolder(tIFolder.getParent(), pMonitor);
				tIFolder.create(false, false, pMonitor);
			}
		} else if (pResource instanceof IFile) {
			createParentFolder(pResource.getParent(), pMonitor);
		}
	}

	public static String getRawLocation(String pWorkspaceRelativeResource) {
		IResource tResource = ResourcesPlugin.getWorkspace().getRoot().findMember(pWorkspaceRelativeResource);
		String tRawLocation;
		if (tResource instanceof Project) {
			Project tProject = (Project) tResource;
			tRawLocation = tProject.getLocation().toFile().toString();
		} else {
			tRawLocation = tResource.getRawLocation().toFile().toString();
		}
		return quotePathToUseInsideOfString(tRawLocation);
	}

	private static String quotePathToUseInsideOfString(String pRawLocation) {
		return pRawLocation.replace('\\', '/');
	}

}
