package de.mallox.eclipse.templates.handler;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.tools.generic.ComparisonDateTool;
import org.apache.velocity.tools.generic.ConversionTool;
import org.apache.velocity.tools.generic.DisplayTool;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;

import de.mallox.eclipse.templates.Activator;
import de.mallox.eclipse.templates.model.ClasspathentryEntry;
import de.mallox.eclipse.templates.model.FileEntry;
import de.mallox.eclipse.templates.model.ProjectDescriptionEntry;
import de.mallox.eclipse.templates.model.ProjectTemplateEntry;
import de.mallox.eclipse.templates.model.TemplateEntry;
import de.mallox.eclipse.templates.model.VariableCombinationEntry;
import de.mallox.eclipse.templates.model.VariableEntry;
import de.mallox.eclipse.templates.model.VariableType;
import de.mallox.eclipse.templates.model.VelocityTool;
import de.mallox.eclipse.templates.model.velocity.VelocityFile;
import de.mallox.eclipse.templates.util.CSVConverter;
import de.mallox.eclipse.templates.util.WorkspaceUtility;
import de.mallox.eclipse.templates.util.compiler.InMemoryCompiler;
import de.mallox.eclipse.templates.velocity.loader.VelocityEngineFactroy;

public class VelocityHandler {

	public VelocityContext initVelocityContext(TemplateEntry pTemplateEntry, String pBaseurl) {

		/*
		 * Workaround: Velocity-Classloading issue:
		 * https://github.com/whitesource/whitesource-bamboo-agent/issues/9
		 */
		Thread tThread = Thread.currentThread();
		ClassLoader tClassLoader = tThread.getContextClassLoader();
		tThread.setContextClassLoader(this.getClass().getClassLoader());
		try {

			/*
			 * Setup Variables:
			 */
			VelocityContext tContext = new VelocityContext();

			/*
			 * Workspaceinformation:
			 */
			IWorkspaceRoot tWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			tContext.put("WorkspaceRoot", tWorkspaceRoot.getRawLocation().toFile().toString());

			List<VariableEntry> tVariableEntryList = pTemplateEntry.getVariable();
			skipThisVariableEntry: for (VariableEntry tVariableEntry : tVariableEntryList) {

				/*
				 * Skip optional variables:
				 */
				if (tVariableEntry.isOptional()
						&& (tVariableEntry.getValue() == null && tVariableEntry.getValue().length() == 0)) {
					continue skipThisVariableEntry;
				}

				if (VariableType.BOOLEAN.equals(tVariableEntry.getType())) {
					tContext.put(tVariableEntry.getName(), Boolean.valueOf(tVariableEntry.getValue()));
				} else if (VariableType.INT.equals(tVariableEntry.getType())) {
					tContext.put(tVariableEntry.getName(), new Integer(tVariableEntry.getValue()));
				} else if (VariableType.STRING_ARRAY.equals(tVariableEntry.getType())) {
					tContext.put(tVariableEntry.getName(), CSVConverter.parseStringArray(tVariableEntry.getValue()));
				} else if (VariableType.PROJECT_FILE_ARRAY.equals(tVariableEntry.getType())) {
					String[] tValues = CSVConverter.parseStringArray(tVariableEntry.getValue());

					if (tValues != null && tValues.length != 0) {
						String tWorkspaceRelativeLocation = tValues[0];
						String tRawLocation = WorkspaceUtility.getRawLocation(tWorkspaceRelativeLocation);

						VelocityFile[] tVelocityFiles = new VelocityFile[tValues.length - 1];
						for (int i = 1; i < tValues.length; i++) {
							tVelocityFiles[i - 1] = new VelocityFile(tValues[i], tWorkspaceRelativeLocation,
									tRawLocation);
						}
						tContext.put(tVariableEntry.getName(), tVelocityFiles);
					}
				} else if (VariableType.PROJECT_FOLDER.equals(tVariableEntry.getType())) {
					String tLocation = tVariableEntry.getValue();
					String tRawLocation = WorkspaceUtility.getRawLocation(tLocation);
					tContext.put(tVariableEntry.getName(), new VelocityFile("", tLocation, tRawLocation));
				} else if (VariableType.PROJECT_FILE.equals(tVariableEntry.getType())) {
					String tRelativeFile = tVariableEntry.getValue();
					if (tRelativeFile != null) {
						String tFileName = tRelativeFile.replaceFirst(".*[\\/]", "");
						String tLocation = tRelativeFile.substring(0, tRelativeFile.length() - tFileName.length() - 1);
						String tRawLocation = WorkspaceUtility.getRawLocation(tLocation);
						tContext.put(tVariableEntry.getName(), new VelocityFile(tFileName, tLocation, tRawLocation));
					}
				} else {
					tContext.put(tVariableEntry.getName(), tVariableEntry.getValue());
				}
			}

			/*
			 * Resolve combined variables:
			 */
			VelocityEngine tVelocityEngine = new VelocityEngine();
			tVelocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "string");
			tVelocityEngine.setProperty("string." + RuntimeConstants.RESOURCE_LOADER + ".class",
					StringResourceLoader.class.getName());
			tVelocityEngine.init();

			StringResourceRepository tResourceRepository = StringResourceLoader.getRepository();
			for (VariableCombinationEntry tVariableCombination : pTemplateEntry.getVariableCombinations()) {
				tResourceRepository.putStringResource(tVariableCombination.getName(),
						tVariableCombination.getPattern());
			}

			for (VariableCombinationEntry tVariableCombination : pTemplateEntry.getVariableCombinations()) {
				Template tTemplate = tVelocityEngine.getTemplate(tVariableCombination.getName());

				StringWriter tStringWriter = new StringWriter();
				tTemplate.merge(tContext, tStringWriter);

				if (VariableType.BOOLEAN.equals(tVariableCombination.getType())) {
					tContext.put(tVariableCombination.getName(), Boolean.valueOf(tStringWriter.toString()));
				} else if (VariableType.INT.equals(tVariableCombination.getType())) {
					tContext.put(tVariableCombination.getName(), new Integer(tStringWriter.toString()));
				} else if (VariableType.STRING_ARRAY.equals(tVariableCombination.getType())) {
					String[] tValues = CSVConverter.parseStringArray(tStringWriter.toString());
					tContext.put(tVariableCombination.getName(), tValues);
				} else if (VariableType.PROJECT_FILE_ARRAY.equals(tVariableCombination.getType())) {
					String[] tValues = CSVConverter.parseStringArray(tStringWriter.toString());

					if (tValues != null && tValues.length != 0) {
						String tWorkspaceRelativeLocation = tValues[0];
						String tRawLocation = WorkspaceUtility.getRawLocation(tWorkspaceRelativeLocation);

						VelocityFile[] tVelocityFiles = new VelocityFile[tValues.length - 1];
						for (int i = 1; i < tValues.length; i++) {
							tVelocityFiles[i - 1] = new VelocityFile(tValues[i], tWorkspaceRelativeLocation,
									tRawLocation);
						}
						tContext.put(tVariableCombination.getName(), tVelocityFiles);
					}

				} else if (VariableType.PROJECT_FOLDER.equals(tVariableCombination.getType())) {
					String tLocation = tStringWriter.toString();
					String tRawLocation = WorkspaceUtility.getRawLocation(tLocation);
					tContext.put(tVariableCombination.getName(), new VelocityFile("", tLocation, tRawLocation));
				} else if (VariableType.PROJECT_FILE.equals(tVariableCombination.getType())) {
					String tRelativeFile = tStringWriter.toString();
					if (tRelativeFile != null) {
						String tFileName = tRelativeFile.replaceFirst(".*[\\/]", "");
						String tLocation = tRelativeFile.substring(0, tRelativeFile.length() - tFileName.length() - 1);
						String tRawLocation = WorkspaceUtility.getRawLocation(tLocation);
						tContext.put(tVariableCombination.getName(),
								new VelocityFile(tFileName, tLocation, tRawLocation));
					}
				} else {
					tContext.put(tVariableCombination.getName(), tStringWriter.toString());
				}
			}

			/*
			 * Additional variables:
			 */
			tContext.put("WorkspaceRoot", tWorkspaceRoot.getFullPath().toFile().toString());
			tContext.put("Template", pTemplateEntry);
			tContext.put("GeneratorPluginVersion", Activator.getVersion().toString());
			tContext.put("date", new ComparisonDateTool());
			tContext.put("display", new DisplayTool());
			tContext.put("convert", new ConversionTool());

			List<VelocityTool> tVelocityTools = pTemplateEntry.getVelocityTools();
			if (tVelocityTools != null && tVelocityTools.size() != 0) {
				loadVelocityTools(pBaseurl, tContext, pTemplateEntry);

				for (VelocityTool tVelocityTool : tVelocityTools) {
					if (tVelocityTool.getInstance() == null) {
						throw new RuntimeException(
								"VelocityTool: '" + tVelocityTool.getVelocityName() + "' with implementation '"
										+ tVelocityTool.getClassName() + "' has been not instantiated.");
					}
					tContext.put(tVelocityTool.getVelocityName(), tVelocityTool.getInstance());
				}
			}

			return tContext;
		} finally {
			tThread.setContextClassLoader(tClassLoader);
		}
	}

	public void resolveProjectTemplate(ProjectTemplateEntry pProjectTemplateEntry, VelocityContext pContext) {
		/*
		 * Workaround: Velocity-Classloading issue:
		 * https://github.com/whitesource/whitesource-bamboo-agent/issues/9
		 */
		Thread tThread = Thread.currentThread();
		ClassLoader tClassLoader = tThread.getContextClassLoader();
		tThread.setContextClassLoader(this.getClass().getClassLoader());
		try {

			/*
			 * Init Velocity:
			 */
			VelocityEngine tVelocityEngine = new VelocityEngine();
			tVelocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "string");
			tVelocityEngine.setProperty("string." + RuntimeConstants.RESOURCE_LOADER + ".class",
					StringResourceLoader.class.getName());
			tVelocityEngine.init();

			StringResourceRepository tResourceRepository = StringResourceLoader.getRepository();

			/*
			 * resolve project-name:
			 */
			tResourceRepository.putStringResource("ProjectName#" + pProjectTemplateEntry.getName(),
					pProjectTemplateEntry.getName());

			Template tTemplate = tVelocityEngine.getTemplate("ProjectName#" + pProjectTemplateEntry.getName());

			StringWriter tStringWriter = new StringWriter();
			tTemplate.merge(pContext, tStringWriter);

			pProjectTemplateEntry.setResolvedName(tStringWriter.toString());

			/*
			 * resolve condition:
			 */
			if (pProjectTemplateEntry.getCondition() != null) {
				tResourceRepository.putStringResource("Condition#" + pProjectTemplateEntry.getCondition(),
						pProjectTemplateEntry.getCondition());

				tTemplate = tVelocityEngine.getTemplate("Condition#" + pProjectTemplateEntry.getCondition());

				tStringWriter = new StringWriter();
				tTemplate.merge(pContext, tStringWriter);

				pProjectTemplateEntry.setResolvedCondition(tStringWriter.toString());
			}

			/*
			 * resolve launcher:
			 */
			for (String tLauncher : pProjectTemplateEntry.getLauncher()) {
				tResourceRepository.putStringResource("Launcher#" + tLauncher, tLauncher);

				tTemplate = tVelocityEngine.getTemplate("Launcher#" + tLauncher);

				tStringWriter = new StringWriter();
				tTemplate.merge(pContext, tStringWriter);

				pProjectTemplateEntry.getResolvedLauncher().add(tStringWriter.toString());
			}

			/*
			 * resolve destination and condition:
			 */
			for (FileEntry tFileEntry : pProjectTemplateEntry.getFiles()) {
				/*
				 * resolve destination:
				 */
				tResourceRepository.putStringResource(tFileEntry.getSrc(), tFileEntry.getDest());
				tTemplate = tVelocityEngine.getTemplate(tFileEntry.getSrc());

				tStringWriter = new StringWriter();
				tTemplate.merge(pContext, tStringWriter);

				tFileEntry.setResolvedDest(tStringWriter.toString());

				/*
				 * resolve condition:
				 */
				if (tFileEntry.getCondition() != null) {
					tResourceRepository.putStringResource("Condition#" + tFileEntry.getSrc(),
							tFileEntry.getCondition());
					tTemplate = tVelocityEngine.getTemplate("Condition#" + tFileEntry.getSrc());

					tStringWriter = new StringWriter();
					tTemplate.merge(pContext, tStringWriter);

					tFileEntry.setResolvedCondition(tStringWriter.toString());
				}
			}

			/*
			 * resolve project dependencies:
			 */
			ProjectDescriptionEntry tTemplateProjectDescription = pProjectTemplateEntry.getProjectDescription();
			if (tTemplateProjectDescription != null) {
				List<String> tTemplateProjectDependencies = tTemplateProjectDescription.getProjectDependencies();
				if (tTemplateProjectDependencies != null) {
					List<String> tResolvedTemplateProjectDependencies = new ArrayList<>();
					for (String tTemplateProjectDependency : tTemplateProjectDependencies) {
						tResourceRepository.putStringResource("ProjectDependency#" + tTemplateProjectDependency,
								tTemplateProjectDependency);

						tTemplate = tVelocityEngine.getTemplate("ProjectDependency#" + tTemplateProjectDependency);

						tStringWriter = new StringWriter();
						tTemplate.merge(pContext, tStringWriter);

						tResolvedTemplateProjectDependencies.add(tStringWriter.toString());
					}
					tTemplateProjectDescription.setResolvedProjectDependencies(tResolvedTemplateProjectDependencies);
				}

			}

			/*
			 * resolve classpath variables:
			 */
			List<ClasspathentryEntry> tClasspathentries = pProjectTemplateEntry.getClasspathentries();
			if (tClasspathentries != null) {
				for (ClasspathentryEntry tClasspathentryEntry : tClasspathentries) {
					String tPath = tClasspathentryEntry.getPath();

					tResourceRepository.putStringResource("ClasspathentryEntry#" + tPath, tPath);

					tTemplate = tVelocityEngine.getTemplate("ClasspathentryEntry#" + tPath);

					tStringWriter = new StringWriter();
					tTemplate.merge(pContext, tStringWriter);

					tClasspathentryEntry.setResolvedPath(tStringWriter.toString());
				}

			}

		} finally {
			tThread.setContextClassLoader(tClassLoader);
		}

	}

	public void copyTemplateFile(IProject pProject, String pBaseurl, VelocityContext pContext,
			List<FileEntry> pFileEntries, IProgressMonitor pMonitor) {

		/*
		 * Workaround: Velocity-Classloading issue:
		 * https://github.com/whitesource/whitesource-bamboo-agent/issues/9
		 */
		Thread tThread = Thread.currentThread();
		ClassLoader tClassLoader = tThread.getContextClassLoader();
		tThread.setContextClassLoader(this.getClass().getClassLoader());
		try {
			// ... Velocity library call(s) ...

			String tDefaultEncoding = pProject.getDefaultCharset();
	
			/*
			 * Template-Files laden:
			 */
			VelocityEngine tVelocityEngine = VelocityEngineFactroy.create(pBaseurl);
			pContext.put("CallbackTemplateHandler", new CallbackTemplateHandler(this, pProject, pBaseurl, pContext,
					new SubProgressMonitor(pMonitor, 1)));
			tVelocityEngine.init();

			nextFileEntry: for (FileEntry tFileEntry : pFileEntries) {

				if (tFileEntry.getResolvedCondition() != null && tFileEntry.getConditionValue() != null) {
					if (!tFileEntry.getResolvedCondition().equals(tFileEntry.getConditionValue())) {
						continue nextFileEntry;
					}
				}

				String tEncoding = tFileEntry.getEncoding();
				if (tEncoding == null || tEncoding.length() == 0) {
					tEncoding = tDefaultEncoding;
				}
				Template tVelocityTemplate = tVelocityEngine.getTemplate(tFileEntry.getSrc(), tEncoding);

				StringWriter tStringWriter = new StringWriter();
				tVelocityTemplate.merge(pContext, tStringWriter);
				InputStream inputStream = null;
				try {
					inputStream = new ByteArrayInputStream(tStringWriter.toString().getBytes(tEncoding));
				} catch (UnsupportedEncodingException e1) {
					Activator.logError("VelocityHandler.copyTemplateFile(...): " + e1.getMessage(), e1);
				}

				Path tPath = new Path(tFileEntry.getResolvedDest());
				IFile tIFile = pProject.getFile(tPath);
				try {
					WorkspaceUtility.createParentFolder(tIFile, pMonitor);
					if (tIFile.exists()) {
						tIFile.delete(true, pMonitor);
					}
					tIFile.create(inputStream, true, pMonitor);
				} catch (CoreException e) {
					Activator.logError("VelocityHandler.copyTemplateFile(...): " + e.getMessage(), e);
				}
			}

		} catch (CoreException e2) {
			Activator.logError("VelocityHandler.copyTemplateFile(...): " + e2.getMessage(), e2);
		} finally {
			tThread.setContextClassLoader(tClassLoader);
		}

	}

	private void loadVelocityTools(String pBaseurl, VelocityContext pContext, TemplateEntry pTemplateEntry) {
		/*
		 * Workaround: Velocity-Classloading issue:
		 * https://github.com/whitesource/whitesource-bamboo-agent/issues/9
		 */
		Thread tThread = Thread.currentThread();
		ClassLoader tClassLoader = tThread.getContextClassLoader();
		final ClassLoader tCurrentClassLoader = this.getClass().getClassLoader();
		tThread.setContextClassLoader(tCurrentClassLoader);
		try {

			/*
			 * VelocityTool-Class-Files laden:
			 */
			VelocityEngine tVelocityEngine = VelocityEngineFactroy.create(pBaseurl);
			tVelocityEngine.init();

			for (VelocityTool tVelocityTool : pTemplateEntry.getVelocityTools()) {

				try {
					Template tVelocityTemplate = tVelocityEngine.getTemplate(
							"velocityTools/" + tVelocityTool.getClassName().replace('.', '/') + ".java", tVelocityTool.getEncoding());

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(baos));
					tVelocityTemplate.merge(pContext, writer);
					writer.close();
					String tJavaSource = baos.toString();

					Class<?> tClass = new InMemoryCompiler().compileClass(tJavaSource, tVelocityTool.getClassName());

					if (tClass == null) {
						throw new RuntimeException(
								"Could not create Velocity-Tool '" + tVelocityTool.getClassName() + "'.");
					}
					tVelocityTool.setInstance(tClass.newInstance());
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		} finally {
			tThread.setContextClassLoader(tClassLoader);
		}

	}

}
