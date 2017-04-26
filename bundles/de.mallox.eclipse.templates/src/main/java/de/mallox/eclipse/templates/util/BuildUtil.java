/**
 * 
 */
package de.mallox.eclipse.templates.util;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspace.ProjectOrder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;

import de.mallox.eclipse.templates.Activator;

/**
 * @author A. Bieber
 */
public class BuildUtil {

	public static int maxBuildTries = 1;

	private static void checkUserCancelled(IProgressMonitor monitor) {
		if (monitor != null && monitor.isCanceled())
			throw new OperationCanceledException();
	}

	private static void waitInWorkaround(int thisTry, IProgressMonitor monitor) {
		monitor.beginTask("Waiting", 1);
		monitor.subTask("Waiting");
		checkUserCancelled(monitor);
		try {
			Thread.sleep(50 * thisTry);
		} catch (InterruptedException e) {
		}
		checkUserCancelled(monitor);
		monitor.worked(1);
		monitor.done();
	}

	/**
	 * @param project
	 *           The project
	 * @param monitor
	 *           The progress monitor
	 * @return <code>true</code> if the project has no error markers now -
	 *         <code>false</code> otherwise
	 */
	public static boolean refreshBuildCloseOpenWorkaround(IProject project,
			IProgressMonitor monitor, int maxTries, boolean onlyWithErrorMarkers)
			throws CoreException {
		int totalWork = 11 * maxTries + 2;
		monitor.beginTask(String.format("Trying to remove error markers for '%s'", project
				.getName()), totalWork);
		monitor.subTask(String.format("Trying to remove error markers for '%s'", project
				.getName()));

		if (onlyWithErrorMarkers && !hasErrorMarker(project)) {
			monitor.done();
			return true;
		}
		Activator.logInfo("Try removing error markers for '" + project.getName()
				+ "', max tries: " + maxTries);

		// Part of bugfix: https://www.jfire.org/modules/bugs/view.php?id=1017
		// If any problem occurs during the refresh or the build, we force the
		// reopening (even if there is no problem marker anymore).
		boolean forceCloseReopen = false;

		checkUserCancelled(monitor);

		// refresh project
		try {
			project.refreshLocal(Integer.MAX_VALUE, new SubProgressMonitor(monitor, 1));
		} catch (OperationCanceledException x) {
			throw x;
		} catch (Exception x) {
			// Part of bugfix: https://www.jfire.org/modules/bugs/view.php?id=1017
			forceCloseReopen = true;
			Activator.logError("Refreshing caused an exception!");
			x.printStackTrace();
		}

		checkUserCancelled(monitor);

		// build project in eclipse
		try {
			project.build(IncrementalProjectBuilder.FULL_BUILD, new SubProgressMonitor(
					monitor, 1));
		} catch (OperationCanceledException x) {
			throw x;
		} catch (Exception x) {
			// Part of bugfix: https://www.jfire.org/modules/bugs/view.php?id=1017
			forceCloseReopen = true;
			Activator.logError("Building caused an exception!");
			x.printStackTrace();
		}

		checkUserCancelled(monitor);

		boolean wasOpen = project.isOpen(); // we ensure that an open project will
														// be open at the end - even if the
														// user cancels inbetween.
		try {

			int thisTry = 0;
			while (thisTry < maxTries && (forceCloseReopen || hasErrorMarker(project))) {
				forceCloseReopen = false;

				waitInWorkaround(thisTry, new SubProgressMonitor(monitor, 1));

				// close project
				if (project.isOpen())
					project.close(new SubProgressMonitor(monitor, 1));
				else
					monitor.worked(1);

				waitInWorkaround(thisTry, new SubProgressMonitor(monitor, 1));

				// open project
				if (!project.isOpen())
					project.open(new SubProgressMonitor(monitor, 1));
				else
					monitor.worked(1);

				waitInWorkaround(thisTry, new SubProgressMonitor(monitor, 1));

				// refresh project
				project.refreshLocal(IResource.DEPTH_INFINITE, new SubProgressMonitor(
						monitor, 1));

				waitInWorkaround(thisTry, new SubProgressMonitor(monitor, 1));

				// clean project
				project.build(IncrementalProjectBuilder.CLEAN_BUILD, new SubProgressMonitor(
						monitor, 1));

				waitInWorkaround(thisTry, new SubProgressMonitor(monitor, 1));

				try {
					// build project in eclipse
					project.build(IncrementalProjectBuilder.FULL_BUILD,
							new SubProgressMonitor(monitor, 1));
				} catch (Exception e) {

				}

				waitInWorkaround(thisTry, new SubProgressMonitor(monitor, 1));

				thisTry++;
			}

		} finally {
			try {
				if (wasOpen && !project.isOpen())
					project.open(new NullProgressMonitor());
			} catch (Exception x) {
				Activator.logError("Reopening project failed!");
				x.printStackTrace();
			}
		}

		monitor.done();

		if (hasErrorMarker(project)) {
			Activator.logError("Still have problem markers after max iterations for project: " + project.getName()); //$NON-NLS-1$
			return false;
		}

		return true;
	}

	/**
	 * Check if the given project has error markers.
	 * 
	 * @param project
	 *           The project to check
	 * @return <code>true</code> if the project has error markers -
	 *         <code>false</code> otherwise.
	 * @throws CoreException
	 *            in case of an error
	 */
	public static List<IMarker> getErrorMarkers(IResource project, boolean returnOnFirst) {
		List<IMarker> result = new LinkedList<IMarker>();
		if (!project.exists())
			return result;
		IMarker[] problemMarkers;
		try {
			problemMarkers = project.findMarkers(IMarker.PROBLEM, true, Integer.MAX_VALUE);
		} catch (CoreException e) {
			return result;
		}
		if (problemMarkers != null && problemMarkers.length > 0)
			for (IMarker marker : problemMarkers) {
				int severity;
				try {
					severity = (Integer) marker.getAttribute(IMarker.SEVERITY);
				} catch (CoreException e) {
					continue;
				}
				if (severity == IMarker.SEVERITY_ERROR) {
					result.add(marker);
					if (returnOnFirst) {
						return result;
					}
				}
			}
		return result;
	}

	/**
	 * Check if the given project has error markers.
	 * 
	 * @param project
	 *           The project to check
	 * @return <code>true</code> if the project has error markers -
	 *         <code>false</code> otherwise.
	 * @throws CoreException
	 *            in case of an error
	 */
	public static boolean hasErrorMarker(IResource project) {
		return getErrorMarkers(project, true).size() > 0;
	}

	/**
	 * Check if the given project has error markers.
	 * 
	 * @param project
	 *           The project to check
	 * @return <code>true</code> if the project has error markers -
	 *         <code>false</code> otherwise.
	 * @throws CoreException
	 *            in case of an error
	 */
	public static List<String> getErrorMarkerMessages(IResource project) {
		List<String> result = new LinkedList<String>();
		List<IMarker> errorMarkers = getErrorMarkers(project, false);
		for (IMarker marker : errorMarkers) {
			String msg;
			try {
				msg = (String) marker.getAttribute(IMarker.MESSAGE);
			} catch (CoreException e) {
				msg = "";
			}
			String location = marker.getResource().getFullPath().toString();
			result.add("Found Error: " + msg + ", in: " + location);
		}
		return result;
	}

	/**
	 * @author A. Bieber
	 */
	public static void buildWorkspace(IProgressMonitor monitor) {
		IWorkspace tWorkspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot tWorkspaceRoot = tWorkspace.getRoot();
		try {
			Activator.logInfo("Refreshing workspace.");
			tWorkspaceRoot.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			Activator.logInfo("Refreshing workspace done.");
		} catch (Exception e) {
			Activator.logError("Building workspace failed", e);
		}
		try {
			Activator.logInfo("Performing full build in workspace");
			ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD,
					new NullProgressMonitor());
			Activator.logInfo("Full build in workspace done");
		} catch (Exception e) {
			Activator.logError("Building workspace failed", e);
		}
		if (BuildUtil.hasErrorMarker(tWorkspaceRoot)) {
			Activator.logInfo("Still error markers after workspace build, trying refreshBuildCloseOpenWorkaround");
			IProject[] tProjects = tWorkspaceRoot.getProjects();

			Activator.logInfo("Computing build order...");
			ProjectOrder tOrderedProjects = tWorkspace.computeProjectOrder(tProjects);
			Activator.logInfo("Have ordered projects, doing refreshBuildCloseOpenWorkaround in build order");

			projectLoop: for (IProject tProject : tOrderedProjects.projects) {
				try {
					if (!BuildUtil.refreshBuildCloseOpenWorkaround(tProject,
							new NullProgressMonitor(), maxBuildTries, true)) {
						Activator.logError("Building of project " + tProject.getName()
								+ " in workspace failed, aborting.");
						break projectLoop;
					}
				} catch (CoreException e) {
					Activator.logError("refreshBuildCloseOpen failed", e);
				}
			}
		}
		List<String> tErrorMarkerMessages = BuildUtil
				.getErrorMarkerMessages(tWorkspaceRoot);
		if (tErrorMarkerMessages.size() != 0) {
			Activator.logWarn("Still have error markers in workspace, here they are:");
			for (String tErrorMsg : tErrorMarkerMessages) {
				Activator.logError(tErrorMsg);
			}
		}
	}
	
}
