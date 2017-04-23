package de.mallox.eclipse.templates.commands;

import java.io.InputStream;
import java.util.AbstractList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.internal.Workbench;

import de.mallox.eclipse.templates.Activator;
import de.mallox.eclipse.templates.handler.SaveConfigurationFileHandler;
import de.mallox.eclipse.templates.handler.TemplateHandler;
import de.mallox.eclipse.templates.model.TemplateEntry;
import de.mallox.eclipse.templates.model.TemplateRefEntry;
import de.mallox.eclipse.templates.model.VariableEntry;
import de.mallox.eclipse.templates.model.mapper.ModelMapper;
import de.mallox.eclipse.templates.model.template.Template;
import de.mallox.eclipse.templates.provider.TemplateDataProvider;

/**
 * {@link RunTemplateHandler} is used to run a template using the context-menu
 * from an property-file.
 */
public class RunTemplateHandler extends AbstractHandler {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(final ExecutionEvent pEvent) throws ExecutionException {

		/*
		 * Determined the property-file, which should be used to run the
		 * Template.
		 */
		if (pEvent.getApplicationContext() instanceof EvaluationContext) {
			final EvaluationContext tEvaluationContext = (EvaluationContext) pEvent.getApplicationContext();

			final Object tDefaultVariable = tEvaluationContext.getDefaultVariable();

			if (tDefaultVariable instanceof AbstractList) {
				@SuppressWarnings("rawtypes")
				final AbstractList tArrayList = (AbstractList) tDefaultVariable;

				if (tArrayList.size() == 1 && tArrayList.get(0) instanceof IFile) {
					final IFile tIFile = (IFile) tArrayList.get(0);

					final Workbench tWorkbench = Workbench.getInstance();
					final Job tJob = createJob(tIFile, tWorkbench);
					tJob.schedule();
				}
			}

		}
		return null;
	}

	/**
	 * Create a {@link Job} for executing the template separate from the
	 * ui-thread.
	 * 
	 * @param pTemplatePropertyFile
	 *            {@link IFile}; property-file which will be used to run the
	 *            template.
	 * @param pWorkbench
	 *            {@link Workbench}.
	 * @return {@link Job} for executing the template.
	 */
	static Job createJob(final IFile pTemplatePropertyFile, final Workbench pWorkbench) {
		return new Job("apply template " + pTemplatePropertyFile.getName()) {
			@Override
			protected IStatus run(final IProgressMonitor pMonitor) {

				/*
				 * read properties to determine the referenced template and
				 * template-values:
				 */
				try (final InputStream tPropertyFileContents = pTemplatePropertyFile.getContents()) {

					final Properties tTemplateProperties = new Properties();
					tTemplateProperties.load(tPropertyFileContents);

					final TemplateRefEntry tTemplateRef = createTemplateRef(tTemplateProperties);
					final TemplateEntry tTemplateEntry = readTemplate(tTemplateRef);

					/*
					 * Set all template-parameters using the property-values:
					 */
					final List<VariableEntry> tVariables = tTemplateEntry.getVariable();
					for (final VariableEntry tVariableEntry : tVariables) {
						final String tName = tVariableEntry.getName();

						final String tValue = tTemplateProperties.getProperty(tName);
						tVariableEntry.setValue(tValue);
					}

					/*
					 * Execute the template:
					 */
					applyTemplate(pMonitor, pWorkbench, tTemplateEntry, tTemplateRef);
				} catch (final Exception e) {
					final String tMessage = "An error occurred while processing template configuration '"
							+ pTemplatePropertyFile + "': " + e.getMessage();
					Activator.logError(tMessage, e);
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, tMessage);
				}

				return Status.OK_STATUS;
			}
		};
	}

	/**
	 * Create a {@link TemplateRefEntry} using the property-values.
	 * 
	 * @param pTemplateProperties
	 *            {@link Properties} property-values.
	 * @return {@link TemplateRefEntry}.
	 */
	static TemplateRefEntry createTemplateRef(final Properties pTemplateProperties) {
		final TemplateRefEntry tTemplateRef = new TemplateRefEntry();

		tTemplateRef.setBaseurl(pTemplateProperties.getProperty(SaveConfigurationFileHandler.TEMPLATE_BASEURL));
		tTemplateRef.setFile(pTemplateProperties.getProperty(SaveConfigurationFileHandler.TEMPLATE_FILENAME));

		return tTemplateRef;
	}

	/**
	 * Read {@link TemplateEntry} for the given {@link TemplateRefEntry}.
	 * 
	 * @param pTemplateRef
	 *            {@link TemplateRefEntry} which describe the template to read.
	 * @return {@link TemplateEntry}.
	 */
	static TemplateEntry readTemplate(final TemplateRefEntry pTemplateRef) {
		final TemplateDataProvider tTemplateDataProvider = new TemplateDataProvider();
		final Template tTemplate = tTemplateDataProvider.readTemplate(pTemplateRef);

		return ModelMapper.INSTANCE.mapToInternalModel(tTemplate);
	}

	/**
	 * Execute the template.
	 * 
	 * @param pMonitor
	 *            {@link IProgressMonitor}, monitor, to show actual progress.
	 * @param pWorkbench
	 *            {@link Workbench}.
	 * @param pTemplateEntry
	 *            {@link TemplateEntry}, description of the template to execute.
	 * @param pTemplateRef
	 *            {@link TemplateRefEntry}, reference to the template and its
	 *            resource.
	 */
	static void applyTemplate(final IProgressMonitor pMonitor, final Workbench pWorkbench,
			final TemplateEntry pTemplateEntry, final TemplateRefEntry pTemplateRef) {
		try {
			final TemplateHandler handler = new TemplateHandler();

			try {
				handler.apply(pMonitor, pTemplateEntry, pTemplateRef.getBaseurl());
			} catch (final CoreException e) {
				e.printStackTrace();
				pWorkbench.getDisplay().syncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialog.openError(pWorkbench.getDisplay().getActiveShell(), "Templates",
								"An error occurred while processing template '" + pTemplateRef.getFile() + "': "
										+ e.getStatus().toString());
					}
				});
			} catch (final Exception e) {
				e.printStackTrace();
				pWorkbench.getDisplay().syncExec(new Runnable() {

					@Override
					public void run() {
						MessageDialog.openError(pWorkbench.getDisplay().getActiveShell(), "Templates",
								"An error occurred while processing template '" + pTemplateRef.getFile() + "': "
										+ e.getMessage());
					}
				});
			}
		} catch (final Exception e) {
			e.printStackTrace();
			MessageDialog.openError(pWorkbench.getDisplay().getActiveShell(), "Templates",
					"An error occurred while processing template '" + pTemplateRef.getFile() + "': " + e.getMessage());
		}
	}

}
