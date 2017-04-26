package de.mallox.eclipse.templates.wizards.templates;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import de.mallox.eclipse.templates.Activator;
import de.mallox.eclipse.templates.handler.SaveConfigurationFileHandler;
import de.mallox.eclipse.templates.handler.TemplateHandler;
import de.mallox.eclipse.templates.model.TemplateEntry;
import de.mallox.eclipse.templates.model.TemplateRefEntry;

public class TemplatesWizard extends Wizard implements INewWizard {

	private SelectTemplateWizardPage selectTemplateWizardPage;
	private ConfigureTemplateWizardPage configureTemplateWizardPage;
	private IWorkbench workbench;
	private SaveConfigurationTemplateWizardPage saveConfigurationTemplateWizardPage;

	/**
	 * Actual reference to the template which are used.
	 */
	private TemplateRefEntry templateRef;
	/**
	 * Actual configuration used for the template.
	 */
	private TemplateEntry templateEntry;

	public TemplatesWizard() {
		super();
	}

	@Override
	public void init(IWorkbench pWorkbench, IStructuredSelection pSelection) {
		workbench = pWorkbench;
		setWindowTitle("Templates");
		selectTemplateWizardPage = new SelectTemplateWizardPage(workbench);
		configureTemplateWizardPage = new ConfigureTemplateWizardPage();
		saveConfigurationTemplateWizardPage = new SaveConfigurationTemplateWizardPage();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		addPage(selectTemplateWizardPage);
		addPage(configureTemplateWizardPage);
		addPage(saveConfigurationTemplateWizardPage);
	}

	@Override
	public boolean performFinish() {
		templateRef = getSelectedTemplateRef();
		templateEntry = configureTemplateWizardPage.init(templateRef);

		if (templateEntry == null) {
			workbench.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openError(workbench.getDisplay().getActiveShell(), "Templates", "Template '"
							+ templateRef.getFile() + "' was not found under '" + templateRef.getBaseurl() + "'.");
				}
			});
			return false;
		}

		if (saveConfigurationTemplateWizardPage.saveConfiguration()) {
			saveConfigurationFile(saveConfigurationTemplateWizardPage.getConfigurationFile());
		}
		applyTemplate();

		return true;
	}

	private void saveConfigurationFile(final String pConfigurationFile) {
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor pMonitor) {
					SaveConfigurationFileHandler handler = new SaveConfigurationFileHandler();

					try {
						handler.save(pMonitor, workbench, templateEntry, templateRef, pConfigurationFile);
					} catch (final CoreException e) {
						e.printStackTrace();
						workbench.getDisplay().syncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openError(workbench.getDisplay().getActiveShell(), "Templates",
										"An error occurred while save configuration for template '"
												+ templateRef.getFile() + "': " + e.getStatus().toString());
							}
						});
					} catch (final Exception e) {
						e.printStackTrace();
						workbench.getDisplay().syncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openError(workbench.getDisplay().getActiveShell(), "Templates",
										"An error occurred while save configuration for template '"
												+ templateRef.getFile() + "': " + e.getMessage());
							}
						});
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(workbench.getDisplay().getActiveShell(), "Templates",
					"An error occurred while ave configuration for template '" + templateRef.getFile() + "': "
							+ e.getMessage());
		}
	}

	private void applyTemplate() {
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor pMonitor) {
					TemplateHandler handler = new TemplateHandler();

					try {
						handler.apply(pMonitor, templateEntry, templateRef.getBaseurl());
					} catch (final CoreException e) {
						e.printStackTrace();
						workbench.getDisplay().syncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openError(workbench.getDisplay().getActiveShell(), "Templates",
										"An error occurred while processing template '" + templateRef.getFile() + "': "
												+ e.getStatus().toString());
							}
						});
					} catch (final Exception e) {
						e.printStackTrace();
						workbench.getDisplay().syncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openError(workbench.getDisplay().getActiveShell(), "Templates",
										"An error occurred while processing template '" + templateRef.getFile() + "': "
												+ e.getMessage());
							}
						});
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(workbench.getDisplay().getActiveShell(), "Templates",
					"An error occurred while processing template '" + templateRef.getFile() + "': " + e.getMessage());
		}
	}

	@Override
	public IWizardPage getNextPage(IWizardPage pPage) {
		IWizardPage tNextPage = super.getNextPage(pPage);

		if (tNextPage == configureTemplateWizardPage) {
			try {
				configureTemplateWizardPage.init(getSelectedTemplateRef());
			} catch (Exception e) {
				Status tStatus = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
				ErrorDialog.openError(getShell(), getWindowTitle(), e.getMessage(), tStatus);
				Activator.logError(e.getMessage(), e);
			}
		}
		if (tNextPage == saveConfigurationTemplateWizardPage) {
			try {
				saveConfigurationTemplateWizardPage.init(getSelectedTemplateRef());
			} catch (Exception e) {
				Status tStatus = new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e);
				ErrorDialog.openError(getShell(), getWindowTitle(), e.getMessage(), tStatus);
				Activator.logError(e.getMessage(), e);
			}
		}
		return tNextPage;
	}

	public TemplateRefEntry getSelectedTemplateRef() {
		return selectTemplateWizardPage.getSelectedTemplateRef();
	}

}
