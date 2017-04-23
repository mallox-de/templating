package de.mallox.eclipse.templates.wizards.create;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import de.mallox.eclipse.templates.handler.template.CreateTemplateHandler;

public class CreateTemplateWizard extends Wizard implements INewWizard {

	private IWorkbench workbench;
	private SelectProjectResourcesWizardPage selectProjectResourcesWizardPage;

	public CreateTemplateWizard() {
		super();
	}

	@Override
	public void init(IWorkbench pWorkbench, IStructuredSelection pSelection) {
		workbench = pWorkbench;
		setWindowTitle("Create Template");
		selectProjectResourcesWizardPage = new SelectProjectResourcesWizardPage();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		addPage(selectProjectResourcesWizardPage);
	}
	
	@Override
	public boolean performFinish() {
		
		final List<IResource> tSelectedResources = selectProjectResourcesWizardPage.getSelectedResourcesIterator();
		final IPath tDestinationPath = selectProjectResourcesWizardPage.getDestinationPath();

		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) {
					try {
						CreateTemplateHandler.createTemplate(tDestinationPath, tSelectedResources, monitor);
					} catch (final Exception e) {
						workbench.getDisplay().syncExec(new Runnable() {
							@Override
							public void run() {
								MessageDialog.openError(workbench.getDisplay().getActiveShell(), "Create new template",
										"An error occurred while create new template: " + e.getMessage());
							}
						});
					}
				}
			});
		} catch (Exception e) {
			MessageDialog.openError(workbench.getDisplay().getActiveShell(), "Create new template",
					"An error occurred while create new template: " + e.getMessage());
		}

		return true;
	}

}
