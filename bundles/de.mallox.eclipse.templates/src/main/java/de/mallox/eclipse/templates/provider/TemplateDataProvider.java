package de.mallox.eclipse.templates.provider;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbench;

import de.mallox.eclipse.templates.model.TemplateIndex;
import de.mallox.eclipse.templates.model.TemplateIndexEntry;
import de.mallox.eclipse.templates.model.TemplateRefEntry;
import de.mallox.eclipse.templates.model.mapper.ModelMapper;
import de.mallox.eclipse.templates.model.template.Template;
import de.mallox.eclipse.templates.preferences.TemplatePreferenceData;
import de.mallox.eclipse.templates.preferences.TemplatePreferenceDataList;
import de.mallox.eclipse.templates.velocity.loader.VelocityEngineFactroy;

public class TemplateDataProvider {
	private final ModelMapper mapper = ModelMapper.INSTANCE;
	private TemplatePreferenceProvider preferenceProvider = new TemplatePreferenceProvider();

	public List<TemplateIndexEntry> readTemplateIndexs(final IWorkbench pWorkspace) {
		/*
		 * Workaround: Velocity-Classloading issue:
		 * https://github.com/whitesource/whitesource-bamboo-agent/issues/9
		 */
		Thread tThread = Thread.currentThread();
		ClassLoader tClassLoader = tThread.getContextClassLoader();
		tThread.setContextClassLoader(getClass().getClassLoader());
		try {
			// ... Velocity library call(s) ...

			List<TemplateIndexEntry> tTemplateIndexs = new ArrayList<>();

			TemplatePreferenceDataList tPreferenceDataList = preferenceProvider.readPreference();

			for (final TemplatePreferenceData tPreferenceData : tPreferenceDataList.getTemplatePreferenceDataList()) {
				try {
					VelocityEngine tVelocityEngine = VelocityEngineFactroy.create(tPreferenceData.getBaseurl());
					tVelocityEngine.init();

					org.apache.velocity.Template tTemplate = tVelocityEngine.getTemplate(tPreferenceData.getDescfile());

					StringWriter tStringWriter = new StringWriter();
					tTemplate.merge(new VelocityContext(), tStringWriter);

					InputStream tInputStream = new ByteArrayInputStream(tStringWriter.toString().getBytes());

					JAXBContext tJaxbContext = JAXBContext.newInstance(TemplateIndex.class);

					Unmarshaller tUnmarshaller = tJaxbContext.createUnmarshaller();
					TemplateIndex tTemplateIndex = (TemplateIndex) tUnmarshaller.unmarshal(tInputStream);

					tTemplateIndexs.add(mapper.mapToInternalModel(tTemplateIndex));
				} catch (final Exception e) {
					pWorkspace.getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							MessageDialog.openError(pWorkspace.getDisplay().getActiveShell(), "Templates",
									"An error occurred while accessing '" + tPreferenceData.getBaseurl() + "': "
											+ e.getMessage());
						}
					});
				}
			}

			return tTemplateIndexs;
		} finally {
			tThread.setContextClassLoader(tClassLoader);
		}

	}

	public Template readTemplate(TemplateRefEntry pTemplateRef) {
		/*
		 * Workaround: Velocity-Classloading issue:
		 * https://github.com/whitesource/whitesource-bamboo-agent/issues/9
		 */
		Thread tThread = Thread.currentThread();
		ClassLoader tClassLoader = tThread.getContextClassLoader();
		tThread.setContextClassLoader(getClass().getClassLoader());
		try {
			// ... Velocity library call(s) ...
			try {
				VelocityEngine tVelocityEngine = VelocityEngineFactroy.create(pTemplateRef.getBaseurl());
				tVelocityEngine.init();

				org.apache.velocity.Template tVelocityTemplate = tVelocityEngine.getTemplate(pTemplateRef.getFile());

				StringWriter tStringWriter = new StringWriter();
				tVelocityTemplate.merge(new VelocityContext(), tStringWriter);

				InputStream tInputStream = new ByteArrayInputStream(tStringWriter.toString().getBytes());

				JAXBContext tJaxbContext = JAXBContext.newInstance(Template.class);

				Unmarshaller tUnmarshaller = tJaxbContext.createUnmarshaller();
				Template tTemplate = (Template) tUnmarshaller.unmarshal(tInputStream);

				if (tTemplate == null) {
					throw new RuntimeException("Das Template '" + pTemplateRef.getFile() + "' konnte nicht unter '"
							+ pTemplateRef.getBaseurl() + "' konnte nicht geladen werden.");
				}

				return tTemplate;
			} catch (UnmarshalException e) {
				throw new RuntimeException("Template '" + pTemplateRef.getBaseurl() + pTemplateRef.getFile()
						+ "' could not be processed: " + e.getLinkedException().getMessage(), e);
			} catch (Exception e) {
				throw new RuntimeException("Template '" + pTemplateRef.getBaseurl() + pTemplateRef.getFile()
						+ "' could not be processed: " + e.getMessage(), e);
			}
		} finally {
			tThread.setContextClassLoader(tClassLoader);
		}

	}
}
