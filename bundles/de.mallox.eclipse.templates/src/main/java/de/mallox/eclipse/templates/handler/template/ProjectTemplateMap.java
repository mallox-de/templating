package de.mallox.eclipse.templates.handler.template;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import de.mallox.eclipse.templates.Activator;
import de.mallox.eclipse.templates.handler.TemplateHandler;
import de.mallox.eclipse.templates.model.template.Attribute;
import de.mallox.eclipse.templates.model.template.BuildCommand;
import de.mallox.eclipse.templates.model.template.Classpathentry;
import de.mallox.eclipse.templates.model.template.File;
import de.mallox.eclipse.templates.model.template.ProjectDescription;
import de.mallox.eclipse.templates.model.template.ProjectTemplate;
import de.mallox.eclipse.templates.model.template.Template;
import de.mallox.eclipse.templates.model.template.Variable;
import de.mallox.eclipse.templates.model.template.VariableCombination;

/**
 * {@link ProjectTemplateMap} erzeugt zu den einzelnen {@link IProject}s die
 * zugeh�rigen Default-Template-Beschreibungen.
 */
public class ProjectTemplateMap {
	private final List<IProject> iprojects = new ArrayList<IProject>();

	private final Map<IProject, ProjectTemplate> map = new HashMap<>();

	public void add(final IProject pIProject, final String pSource, final String pDestination, String pEncoding) throws CoreException {
		ProjectTemplate tProjectTemplate = map.get(pIProject);

		if (tProjectTemplate == null) {
			tProjectTemplate = new ProjectTemplate();
			tProjectTemplate.setName(replaceTemplateWithPlaceMarkers(pIProject.getName()));

			map.put(pIProject, tProjectTemplate);
			iprojects.add(pIProject);
		}

		/*
		 * Set File-Encoding if it is different to project default encoding:
		 */
		String tDefaultEncoding = pIProject.getDefaultCharset();
		String tEncoding = null;
		if (tDefaultEncoding != null && !tDefaultEncoding.equals(pEncoding)) {
			tEncoding = pEncoding;
		}
		tProjectTemplate.getFiles().add(createFile(pSource, replaceTemplateWithPlaceMarkers(pDestination),  tEncoding));
	}

	private static File createFile(final String pSource, final String pDestination, String pEncoding) {
		final File tFile = new File();

		tFile.setSrc(pSource);
		tFile.setDest(pDestination);
		tFile.setEncoding(pEncoding);

		return tFile;
	}

	public Template getTemplate() throws CoreException {
		final Template tTemplate = new Template();
		tTemplate.setDescription(
				"Created: " + DateFormat.getDateInstance().format(new Date()) + "\nTODO: Add template description.");
		tTemplate.setVersion("0.0.0");
		tTemplate.setGeneratorVersion(Activator.getVersion().toString());
		tTemplate.setName("TODO: Template-Name");

		Variable tVariable = new Variable();
		tVariable.setType("string");
		tVariable.setName("ComponentName");
		tVariable.setDefault("Template");
		tVariable.setDescription("Name of the component");
		tTemplate.getVariable().add(tVariable);

		tVariable = new Variable();
		tVariable.setType("string");
		tVariable.setName("PackagePart");
		tVariable.setDefault("template");
		tVariable.setDescription("Part of package-name, to identify the component.");
		tTemplate.getVariable().add(tVariable);

		final VariableCombination tVariableCombination = new VariableCombination();
		tVariableCombination.setType("string");
		tVariableCombination.setName("PackagePartPath");
		tVariableCombination.setPattern("$PackagePart.replace('.', '/')");
		tVariableCombination.setDescription("Part of path according to the PackagePart.");
		tTemplate.getVariableCombinations().add(tVariableCombination);

		for (final IProject tIProject : iprojects) {
			final ProjectTemplate tProjectTemplate = map.get(tIProject);
			tProjectTemplate.setDescription("TODO: Add project description.");

			tProjectTemplate.setProjectDescription(createProjectDescription(tIProject.getDescription()));

			final File tChangeLogFile = new File();
			tChangeLogFile.setSrc("/" + tIProject.getName() + "/changelog.md.vm");
			tChangeLogFile.setDest("changelog.md");
			tProjectTemplate.getFiles().add(tChangeLogFile);

			if (tIProject.hasNature(JavaCore.NATURE_ID)) {
				createClasspathEntries(tProjectTemplate.getClasspathentries(), JavaCore.create(tIProject));
			}

			tTemplate.getProjectTemplate().add(tProjectTemplate);
		}

		return tTemplate;
	}

	private static void createClasspathEntries(final List<Classpathentry> pClasspathEntryList,
			final IJavaProject pIJavaProject) throws JavaModelException {

		final IClasspathEntry[] tRawClasspath = pIJavaProject.getRawClasspath();
		for (final IClasspathEntry tIClasspathEntry : tRawClasspath) {
			// TODO add all possible attributes...
			final Classpathentry tClasspathentry = new Classpathentry();

			tClasspathentry.setKind(TemplateHandler.kindToString(tIClasspathEntry.getEntryKind()));
			String tClasspathEntryPath = tIClasspathEntry.getPath().toString();

			/*
			 * Source-path of own project must start without '/':
			 */
			final String tRelativeProjectPath = pIJavaProject.getPath().toString();
			if (tClasspathEntryPath.startsWith(tRelativeProjectPath)) {
				tClasspathEntryPath = tClasspathEntryPath.substring(tRelativeProjectPath.length());
				if (tClasspathEntryPath.startsWith("/")) {
					tClasspathEntryPath = tClasspathEntryPath.substring(1);
				}
			}

			tClasspathentry.setPath(tClasspathEntryPath);

			pClasspathEntryList.add(tClasspathentry);
		}
	}

	private static ProjectDescription createProjectDescription(final IProjectDescription pIProjectDescription) {
		final ProjectDescription tProjectDescription = new ProjectDescription();

		createBuilCommands(tProjectDescription.getBuildCommand(), pIProjectDescription);
		createNatures(tProjectDescription.getNature(), pIProjectDescription);
		createProjectDependencies(tProjectDescription.getProjectDependencies(), pIProjectDescription);

		return tProjectDescription;
	}

	private static void createProjectDependencies(final List<String> pProjectDependencies,
			final IProjectDescription pIProjectDescription) {

		final IProject[] tReferencedProjects = pIProjectDescription.getReferencedProjects();
		for (final IProject tIProject : tReferencedProjects) {
			pProjectDependencies.add(tIProject.getName());
		}
	}

	private static void createNatures(final List<String> pNature, final IProjectDescription pIProjectDescription) {
		final String[] tNatureIds = pIProjectDescription.getNatureIds();
		for (final String tNatureId : tNatureIds) {
			pNature.add(tNatureId);
		}
	}

	private static void createBuilCommands(final List<BuildCommand> pBuildCommandList,
			final IProjectDescription pIProjectDescription) {
		final ICommand[] tBuildSpec = pIProjectDescription.getBuildSpec();

		for (final ICommand tICommand : tBuildSpec) {
			final BuildCommand tBuildCommand = new BuildCommand();
			tBuildCommand.setName(tICommand.getBuilderName());

			for (final Object tAttributeName : tICommand.getArguments().keySet()) {
				final Attribute tAttribute = new Attribute();

				tAttribute.setName((String) tAttributeName);
				tAttribute.setValue((String) tICommand.getArguments().get(tAttributeName));

				tBuildCommand.getAttribute().add(tAttribute);
			}

			pBuildCommandList.add(tBuildCommand);
		}
	}

	public List<IProject> getIprojects() {
		return this.iprojects;
	}

	private static String replaceTemplateWithPlaceMarkers(String pDestinationPath) {
		String tReplacedDestinationPath = pDestinationPath.replaceAll("Template", "\\${ComponentName}");
		tReplacedDestinationPath = tReplacedDestinationPath.replaceAll("/template/", "/\\${PackagePartPath}/");
		return tReplacedDestinationPath;
	}
}
