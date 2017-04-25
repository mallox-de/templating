package de.mallox.eclipse.templates.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.ClasspathAttribute;
import org.eclipse.jdt.internal.core.ClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;

import de.mallox.eclipse.templates.Activator;
import de.mallox.eclipse.templates.model.AttributeEntry;
import de.mallox.eclipse.templates.model.BuildCommandEntry;
import de.mallox.eclipse.templates.model.ClasspathentryEntry;
import de.mallox.eclipse.templates.model.ProjectDescriptionEntry;
import de.mallox.eclipse.templates.model.ProjectTemplateEntry;
import de.mallox.eclipse.templates.model.TemplateEntry;
import de.mallox.eclipse.templates.model.VersionChecker;
import de.mallox.eclipse.templates.util.BuildUtil;

public class TemplateHandler {

	@SuppressWarnings("unchecked")
	public void apply(IProgressMonitor pMonitor, final TemplateEntry pTemplateEntry, String pBaseurl) throws Exception {
		checkCompatibilityOfPlugin(pTemplateEntry);

		List<ProjectTemplateEntry> tProjectTemplateEntryList = pTemplateEntry.getProjectTemplate();
		int tWorkAmount = tProjectTemplateEntryList.size();

		pMonitor.beginTask("Erzeuge Projekte: ", tWorkAmount);

		IWorkspaceRoot tWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		/*
		 * init Velocity
		 */
		VelocityHandler tVelocityHandler = new VelocityHandler();
		VelocityContext tVelocityContext = tVelocityHandler.initVelocityContext(pTemplateEntry, pBaseurl);

		nextProjectTemplateEntry: for (ProjectTemplateEntry tProjectTemplateEntry : tProjectTemplateEntryList) {
			pMonitor.subTask(tProjectTemplateEntry.getResolvedName());

			/*
			 * resolve Template
			 */
			tVelocityHandler.resolveProjectTemplate(tProjectTemplateEntry, tVelocityContext);

			if (tProjectTemplateEntry.getResolvedCondition() != null
					&& tProjectTemplateEntry.getConditionValue() != null) {
				if (!tProjectTemplateEntry.getResolvedCondition().equals(tProjectTemplateEntry.getConditionValue())) {
					continue nextProjectTemplateEntry;
				}
			}

			/*
			 * Projekt anlegen:
			 */
			IProject tIProject = tWorkspaceRoot.getProject(tProjectTemplateEntry.getResolvedName());
			SubProgressMonitor tSubMonitor = new SubProgressMonitor(pMonitor, 1);
			if (tProjectTemplateEntry.isAlreadyExists() == null || tProjectTemplateEntry.isAlreadyExists() == false) {
				tIProject.create(tSubMonitor);
				tIProject.open(tSubMonitor);
				tIProject.setDefaultCharset(tProjectTemplateEntry.getEncoding(), new SubProgressMonitor(pMonitor, 1));
			}

			/*
			 * Projekt konfigurieren:
			 */
			ProjectDescriptionEntry tTemplateProjectDescription = tProjectTemplateEntry.getProjectDescription();

			/*
			 * Natures:
			 */
			if (tTemplateProjectDescription != null) {
				List<String> natureList = tTemplateProjectDescription.getNature();
				if (natureList != null && natureList.size() != 0) {
					String[] natureIds = natureList.toArray(new String[natureList.size()]);

					IProjectDescription tIProjectDescription = tIProject.getDescription();
					tIProjectDescription.setNatureIds(natureIds);
					tIProject.setDescription(tIProjectDescription, tSubMonitor);
				}
			}

			/*
			 * Builder:
			 */
			if (tTemplateProjectDescription != null) {
				List<BuildCommandEntry> tTemplateBuildCommands = tTemplateProjectDescription.getBuildCommand();
				if (tTemplateBuildCommands != null && tTemplateBuildCommands.size() != 0) {

					IProjectDescription tIProjectDescription = tIProject.getDescription();
					Map<String, ICommand> tBuildSpecMap = new HashMap<>();
					for (ICommand tBuildSpec : tIProjectDescription.getBuildSpec()) {
						tBuildSpecMap.put(tBuildSpec.getBuilderName(), tBuildSpec);
					}

					for (BuildCommandEntry tTemplateBuildCommand : tTemplateBuildCommands) {
						org.eclipse.core.internal.events.BuildCommand tBbuildCommand = new org.eclipse.core.internal.events.BuildCommand();

						tBbuildCommand.setBuilderName(tTemplateBuildCommand.getName());
						for (AttributeEntry tTemplateAttribute : tTemplateBuildCommand.getAttribute()) {
							tBbuildCommand.getArguments().put(tTemplateAttribute.getName(),
									tTemplateAttribute.getValue());
						}

						tBuildSpecMap.put(tBbuildCommand.getBuilderName(), tBbuildCommand);
					}
					tIProjectDescription
							.setBuildSpec(tBuildSpecMap.values().toArray(new ICommand[tBuildSpecMap.size()]));

					tIProject.setDescription(tIProjectDescription, tSubMonitor);
				}
			}

			/*
			 * Project-dependencies:
			 */
			if (tTemplateProjectDescription != null) {
				List<String> tTemplateProjectDependencies = tTemplateProjectDescription
						.getResolvedProjectDependencies();
				if (tTemplateProjectDependencies != null && tTemplateProjectDependencies.size() != 0) {

					IProject[] tIProjectDependencies = new IProject[tTemplateProjectDependencies.size()];
					for (int i = 0; i < tTemplateProjectDependencies.size(); i++) {
						tIProjectDependencies[i] = tWorkspaceRoot.getProject(tTemplateProjectDependencies.get(i));
					}

					IProjectDescription tIProjectDescription = tIProject.getDescription();
					tIProjectDescription.setReferencedProjects(tIProjectDependencies);

					tIProject.setDescription(tIProjectDescription, tSubMonitor);
				}
			}

			/*
			 * Classpath:
			 */
			if (tProjectTemplateEntry != null) {
				List<ClasspathentryEntry> tTemplateClasspathentries = tProjectTemplateEntry.getClasspathentries();
				if (tTemplateClasspathentries != null && tTemplateClasspathentries.size() != 0) {

					IJavaProject tIJavaProject = JavaCore.create(tIProject);

					// Classpath:
					List<IClasspathEntry> tIClasspathEntries = new ArrayList<IClasspathEntry>();

					boolean tHasContainerRuntimeBeenAdded = false;
					boolean tHasOutputFolderBeenAdded = false;

					for (ClasspathentryEntry tTemplateClasspathentry : tTemplateClasspathentries) {
						List<AttributeEntry> tTemplateAttributes = tTemplateClasspathentry.getAttribute();

						IClasspathAttribute[] tIClasspathAttributes = ClasspathEntry.NO_EXTRA_ATTRIBUTES;
						if (tTemplateAttributes != null) {
							tIClasspathAttributes = new IClasspathAttribute[tTemplateAttributes.size()];
							for (int i = 0; i < tTemplateAttributes.size(); i++) {
								tIClasspathAttributes[i] = new ClasspathAttribute(tTemplateAttributes.get(i).getName(),
										tTemplateAttributes.get(i).getValue());
							}
						}
						int tKind = kindFromString(tTemplateClasspathentry.getKind());
						if (tKind == IClasspathEntry.CPE_CONTAINER) {
							tHasContainerRuntimeBeenAdded = true;
						} else if (tKind == ClasspathEntry.K_OUTPUT) {
							tHasOutputFolderBeenAdded = true;
						}

						ClasspathEntry tClasspathEntry = new ClasspathEntry(IPackageFragmentRoot.K_SOURCE, tKind,
								new Path(tTemplateClasspathentry.getResolvedPath()), ClasspathEntry.INCLUDE_ALL,
								ClasspathEntry.EXCLUDE_NONE, null, null, null, false, null, false,
								tIClasspathAttributes);
						tIClasspathEntries.add(tClasspathEntry);
					}

					// bin-Folder:
					// TODO bin-output folder...
					if (!tHasOutputFolderBeenAdded) {
						IFolder tBinIFolder = tIProject.getFolder("bin");
						if (!tBinIFolder.exists()) {
							try {
								tBinIFolder.create(false, true, tSubMonitor);
								tIJavaProject.setOutputLocation(tBinIFolder.getFullPath(), tSubMonitor);
							} catch (Exception e) {
								System.err.println(e.getMessage());
							}
						}
					}

					if (!tHasContainerRuntimeBeenAdded) {

						/*
						 * If no Container has been added the default runtime
						 * will be added:
						 */
						IPath tContainerIPath = new Path(JavaRuntime.JRE_CONTAINER);
						IVMInstall tVmInstall = JavaRuntime.getDefaultVMInstall();
						IPath tVmIPath = tContainerIPath.append(tVmInstall.getVMInstallType().getId())
								.append(tVmInstall.getName());
						tIClasspathEntries.add(JavaCore.newContainerEntry(tVmIPath));
					}

					/*
					 * add libs to project class path
					 */
					tIJavaProject.setRawClasspath(
							tIClasspathEntries.toArray(new IClasspathEntry[tIClasspathEntries.size()]), tSubMonitor);
				}
			}
			pMonitor.worked(1);

			/*
			 * Add ProjectTemplate to current velocity-context, so the
			 * information can be used in template-files.
			 */
			tVelocityContext.put("ProjectTemplate", tProjectTemplateEntry);

			tVelocityHandler.copyTemplateFile(tIProject, pBaseurl, tVelocityContext, tProjectTemplateEntry.getFiles(),
					pMonitor);

			/*
			 * Copy dynamic created Files
			 */

			/*
			 * Compile Worspace
			 */
			if (tProjectTemplateEntry.isCompileWorkspace() != null
					&& tProjectTemplateEntry.isCompileWorkspace().booleanValue()) {
				IProgressMonitor tSubProgressMonitor = new SubProgressMonitor(pMonitor, 1);
				tSubProgressMonitor.setTaskName("compile workspace...");
				BuildUtil.buildWorkspace(tSubProgressMonitor);
			}
		}

		/*
		 * Call Launcher:
		 */
		nextProjectTemplateEntry: for (ProjectTemplateEntry tProjectTemplateEntry : tProjectTemplateEntryList) {

			if (tProjectTemplateEntry.getResolvedCondition() != null
					&& tProjectTemplateEntry.getConditionValue() != null) {
				if (!tProjectTemplateEntry.getResolvedCondition().equals(tProjectTemplateEntry.getConditionValue())) {
					continue nextProjectTemplateEntry;
				}
			}

			pMonitor.subTask(tProjectTemplateEntry.getResolvedName());
			IProject tIProject = tWorkspaceRoot.getProject(tProjectTemplateEntry.getResolvedName());

			for (String tLauncher : tProjectTemplateEntry.getResolvedLauncher()) {
				IProgressMonitor tSubProgressMonitor = new SubProgressMonitor(pMonitor, 1);
				tSubProgressMonitor.setTaskName("launch " + tLauncher);

				IFile tLauncherFile = tIProject.getFile(tLauncher);
				ILaunchConfiguration tLaunchConfiguration = DebugPlugin.getDefault().getLaunchManager()
						.getLaunchConfiguration(tLauncherFile);
				tLaunchConfiguration.launch(ILaunchManager.RUN_MODE, tSubProgressMonitor);
			}
		}

		pMonitor.done();
	}

	/**
	 * Check if the plugin is compatible with the template to perform; if not an
	 * {@link IllegalArgumentException} is thrown.
	 * 
	 * @param pTemplateEntry
	 *            {@link TemplateEntry}
	 */
	private static void checkCompatibilityOfPlugin(TemplateEntry pTemplateEntry) {
		if (VersionChecker.checkCompatibilityOfPlugin(pTemplateEntry.getGeneratorVersion())) {
			// Version fits:
			return;
		}

		throw new IllegalArgumentException("Caution: Template version '" + pTemplateEntry.getVersion()
				+ "' needs plugin version '" + pTemplateEntry.getGeneratorVersion() + "'. Current plugin version: '"
				+ Activator.getVersion() + "'");
	}

	/**
	 * Quelle: {@link ClasspathEntry} Returns the kind of a
	 * <code>PackageFragmentRoot</code> from its <code>String</code> form.
	 */
	public static int kindFromString(String pKindStr) {

		if (pKindStr.equalsIgnoreCase("prj")) //$NON-NLS-1$
			return IClasspathEntry.CPE_PROJECT;
		if (pKindStr.equalsIgnoreCase("var")) //$NON-NLS-1$
			return IClasspathEntry.CPE_VARIABLE;
		if (pKindStr.equalsIgnoreCase("con")) //$NON-NLS-1$
			return IClasspathEntry.CPE_CONTAINER;
		if (pKindStr.equalsIgnoreCase("src")) //$NON-NLS-1$
			return IClasspathEntry.CPE_SOURCE;
		if (pKindStr.equalsIgnoreCase("lib")) //$NON-NLS-1$
			return IClasspathEntry.CPE_LIBRARY;
		if (pKindStr.equalsIgnoreCase("output")) //$NON-NLS-1$
			return ClasspathEntry.K_OUTPUT;
		return -1;
	}

	/**
	 * Quelle: {@link ClasspathEntry} Returns a <code>String</code> for the kind
	 * of a class path entry.
	 */
	public static String kindToString(int pKind) {

		switch (pKind) {
		case IClasspathEntry.CPE_PROJECT:
			return "src"; // backward compatibility //$NON-NLS-1$
		case IClasspathEntry.CPE_SOURCE:
			return "src"; //$NON-NLS-1$
		case IClasspathEntry.CPE_LIBRARY:
			return "lib"; //$NON-NLS-1$
		case IClasspathEntry.CPE_VARIABLE:
			return "var"; //$NON-NLS-1$
		case IClasspathEntry.CPE_CONTAINER:
			return "con"; //$NON-NLS-1$
		case ClasspathEntry.K_OUTPUT:
			return "output"; //$NON-NLS-1$
		default:
			return "unknown"; //$NON-NLS-1$
		}
	}

}
