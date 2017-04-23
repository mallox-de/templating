package de.mallox.eclipse.templates.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.VelocityContext;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;

import de.mallox.eclipse.templates.model.FileEntry;

public class CallbackTemplateHandler {

	private VelocityHandler velocityHandler;
	private IProgressMonitor monitor;
	private IProject project;
	private String baseurl;
	private VelocityContext context;

	public CallbackTemplateHandler(VelocityHandler pVelocityHandler, IProject pProject, String pBaseurl,
			VelocityContext pContext, IProgressMonitor pMonitor) {
		velocityHandler = pVelocityHandler;
		project = pProject;
		baseurl = pBaseurl;
		context = pContext;
		monitor = pMonitor;
	}

	public String create(String pSource, String pResolvedDest, Object... pParameter) {
		List<FileEntry> tFileEntries = createFileEntry(pSource, pResolvedDest);

		Map<String, Object> tOldParameterInContext = addParameterToContext(pParameter);
		velocityHandler.copyTemplateFile(project, baseurl, context, tFileEntries, monitor);

		restoreOldParameterInContext(tOldParameterInContext);

		return "";
	}

	private void restoreOldParameterInContext(Map<String, Object> pOldParameterInContext) {
		for (String tKey : pOldParameterInContext.keySet()) {
			Object tOldParamter = pOldParameterInContext.get(tKey);

			if (tOldParamter == null) {
				context.remove(tKey);
			} else {
				context.put(tKey, tOldParamter);
			}
		}
	}

	private Map<String, Object> addParameterToContext(Object[] pParameter) {
		Map<String, Object> tBackupObjects = new HashMap<>();
		for (int i = 0; i < pParameter.length / 2; i += 2) {
			String tKey = (String) pParameter[i];
			Object tOldObject = context.put(tKey, pParameter[i + 1]);
			tBackupObjects.put(tKey, tOldObject);
		}
		return tBackupObjects;
	}

	private static List<FileEntry> createFileEntry(String pSource, String pResolvedDest) {
		List<FileEntry> tFileEntries = new ArrayList<>();

		FileEntry tFileEntry = new FileEntry();
		tFileEntry.setSrc(pSource);
		tFileEntry.setDest(pResolvedDest);
		tFileEntry.setResolvedDest(pResolvedDest);
		tFileEntry.setCondition("true");

		tFileEntries.add(tFileEntry);
		return tFileEntries;
	}
}
