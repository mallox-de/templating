package de.mallox.eclipse.templates.handler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IWorkbench;

import de.mallox.eclipse.templates.model.TemplateEntry;
import de.mallox.eclipse.templates.model.TemplateRefEntry;
import de.mallox.eclipse.templates.model.VariableEntry;

public class SaveConfigurationFileHandler {

	/**
	 * Property-Name unter dem die Basis-URL des referenzierten Templates
	 * abgelegt werden soll.
	 */
	public static final String TEMPLATE_BASEURL = "templateBaseurl";

	/**
	 * Property-Name unter dem der Template-Dateiname des referenzierten
	 * Templates abgelegt werden soll.
	 */
	public static final String TEMPLATE_FILENAME = "templateFilename";

	public void save(final IProgressMonitor pMonitor, final IWorkbench pWorkbench, final TemplateEntry pTemplateEntry,
			final TemplateRefEntry pTemplateRef, final String pConfigurationFileName) throws Exception {

		pMonitor.beginTask("Erzeuge Projekte: ", 1);

		IWorkspaceRoot tWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

		Path tPath = new Path(pConfigurationFileName);
		String tProjectName = tPath.segment(0);

		IProject tIProject = tWorkspaceRoot.getProject(tProjectName);
		if (!tIProject.exists()) {
			throw new RuntimeException("Project '" + tIProject.getName() + "' does not exist in Workspace.");
		}

		IFile tConfigurationFile = tIProject.getFile(tPath.removeFirstSegments(1));

		ByteArrayOutputStream tArrayOutputStream = new ByteArrayOutputStream();
		String tEncoding = tIProject.getDefaultCharset();
		OutputStreamWriter tWriter = new OutputStreamWriter(tArrayOutputStream, Charset.forName(tEncoding));

		tWriter.write(TEMPLATE_BASEURL + " = " + pTemplateRef.getBaseurl() + "\n");
		tWriter.write(TEMPLATE_FILENAME + " = " + pTemplateRef.getFile() + "\n");

		List<VariableEntry> tVariables = pTemplateEntry.getVariable();
		for (VariableEntry tVariableEntry : tVariables) {
			tWriter.write(tVariableEntry.getName() + " = "
					+ (tVariableEntry.getValue() != null ? tVariableEntry.getValue() : "") + "\n");
		}
		tWriter.close();
		tArrayOutputStream.close();

		ByteArrayInputStream tByteArrayInputStream = new ByteArrayInputStream(tArrayOutputStream.toByteArray());
		if (tConfigurationFile.exists()) {
			tConfigurationFile.setContents(tByteArrayInputStream, IFile.KEEP_HISTORY, pMonitor);
		} else {
			tConfigurationFile.create(tByteArrayInputStream, IFile.KEEP_HISTORY, pMonitor);
		}
		tConfigurationFile.setCharset(tEncoding, pMonitor);
	}

}
