package de.mallox.eclipse.templates.wizards.templates;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;

import de.mallox.eclipse.templates.Activator;
import de.mallox.eclipse.templates.model.TemplateIndex;
import de.mallox.eclipse.templates.model.TemplateRefEntry;
import de.mallox.eclipse.templates.model.VersionChecker;
import de.mallox.eclipse.templates.model.template.Template;
import de.mallox.eclipse.templates.provider.TemplateDataProvider;
import de.mallox.eclipse.templates.widgets.templates.TemplatesContentProvider;
import de.mallox.eclipse.templates.widgets.templates.TemplatesLabelProvider;

public class SelectTemplateWizardPage extends WizardPage implements IWizardPage {

	private Composite container;

	private TemplateRefEntry selectedTemplate;

	private TreeViewer templatesTreeViewer;
	private Text descriptionText;

	private IWorkbench workbench;

	public SelectTemplateWizardPage(IWorkbench pWorkbench) {
		super("Templates");
		workbench = pWorkbench;
		setTitle("Templates");
		setDescription("Select template.");
	}

	@Override
	public void createControl(Composite pParent) {
		container = new Composite(pParent, SWT.NONE);
		GridLayout tLayout = new GridLayout();
		tLayout.numColumns = 1;
		tLayout.makeColumnsEqualWidth = true;
		container.setLayout(tLayout);

		templatesTreeViewer = new TreeViewer(container, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		templatesTreeViewer.setContentProvider(new TemplatesContentProvider());
		templatesTreeViewer.setLabelProvider(new TemplatesLabelProvider());
		templatesTreeViewer.setInput(new TemplateDataProvider().readTemplateIndexs(workbench));
		templatesTreeViewer.expandAll();
		GridData tLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tLayoutData.heightHint = 1;
		templatesTreeViewer.getControl().setLayoutData(tLayoutData);

		templatesTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent pEvent) {
				// if the selection is empty clear the label
				if (pEvent.getSelection().isEmpty()) {
					setPageComplete(false);
					descriptionText.setText("");
					selectedTemplate = null;
					setPageComplete(false);
					return;
				}
				if (pEvent.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection tSelection = (IStructuredSelection) pEvent.getSelection();
					if (tSelection.size() == 1) {
						Object tSelectedElement = tSelection.getFirstElement();

						if (tSelectedElement instanceof TemplateIndex) {
							TemplateIndex tTemplateIndex = (TemplateIndex) tSelectedElement;
							descriptionText.setText(tTemplateIndex.getDescription());
							selectedTemplate = null;
							setPageComplete(false);
							return;

						} else if (tSelectedElement instanceof TemplateRefEntry) {
							selectedTemplate = (TemplateRefEntry) tSelectedElement;
							if (selectedTemplate.getDescription() == null) {
								// TODO evtl. über ein Future die Daten im
								// Hintergrund lesen...
								Template tTemplate = new TemplateDataProvider().readTemplate(selectedTemplate);
								selectedTemplate.setGeneratorVersionCompatibleWithTemplate(
										VersionChecker.checkCompatibilityOfPlugin(tTemplate.getGeneratorVersion()));
								if (selectedTemplate.isGeneratorVersionCompatibleWithTemplate()) {
									selectedTemplate.setDescription(tTemplate.getDescription());
								} else {
									selectedTemplate
											.setDescription("Caution: Template version '" + tTemplate.getVersion()
													+ "' needs plugin version '" + tTemplate.getGeneratorVersion()
													+ "'. Current plugin version: '" + Activator.getVersion() + "'");
								}
							}
							descriptionText.setText(selectedTemplate.getDescription());

							setPageComplete(selectedTemplate.isGeneratorVersionCompatibleWithTemplate());
							return;
						}
					}
				}
				descriptionText.setText("");
				selectedTemplate = null;
				setPageComplete(false);
			}
		});

		descriptionText = new Text(container, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		descriptionText.setEditable(false);

		tLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tLayoutData.heightHint = 1;
		descriptionText.setLayoutData(tLayoutData);

		// required to avoid an error in the system
		setControl(container);
		setPageComplete(false);
	}

	public TemplateRefEntry getSelectedTemplateRef() {
		return selectedTemplate;
	}
}
