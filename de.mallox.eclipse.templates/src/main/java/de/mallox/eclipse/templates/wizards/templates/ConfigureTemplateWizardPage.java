package de.mallox.eclipse.templates.wizards.templates;

import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.mallox.eclipse.templates.model.TemplateEntry;
import de.mallox.eclipse.templates.model.TemplateRefEntry;
import de.mallox.eclipse.templates.model.VariableEntry;
import de.mallox.eclipse.templates.model.mapper.ModelMapper;
import de.mallox.eclipse.templates.model.template.Template;
import de.mallox.eclipse.templates.provider.TemplateDataProvider;
import de.mallox.eclipse.templates.widgets.variables.VariablesTableViewer;

public class ConfigureTemplateWizardPage extends WizardPage implements IWizardPage {

	private Composite container;
	private TemplateEntry templateEntry;
	private VariablesTableViewer tableViewer;
	private TemplateRefEntry templateRef;

	public ConfigureTemplateWizardPage() {
		super("Templates");
		setTitle("Templates");
		setDescription("Configure template.");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout tLayout = new GridLayout();
		tLayout.numColumns = 2;
		tLayout.makeColumnsEqualWidth = true;
		container.setLayout(tLayout);

		tableViewer = new VariablesTableViewer(container, this);

		// required to avoid an error in the system
		setControl(container);
		update();
	}

	public void update() {
		if (templateEntry == null) {
			setPageComplete(false);
			return;
		}

		List<VariableEntry> tVariables = templateEntry.getVariable();
		for (VariableEntry tVariableEntry : tVariables) {
			String tValue = tVariableEntry.getValue();
			if (!tVariableEntry.isOptional() && (tValue == null || tValue.length() == 0)) {
				setMessage("Missing value for '" + tVariableEntry.getName() + "'.");

				setPageComplete(false);
				return;
			}
		}
		setMessage(null);
		setPageComplete(true);
	}

	public TemplateEntry init(TemplateRefEntry pTemplateRef) {
		/*
		 * Actualization is only required if a new templateRef has been
		 * selected:
		 */
		if (this.templateRef == pTemplateRef) {
			return templateEntry;
		}
		templateRef = pTemplateRef;

		TemplateDataProvider tTemplateDataProvider = new TemplateDataProvider();
		Template tTemplate = tTemplateDataProvider.readTemplate(pTemplateRef);

		templateEntry = ModelMapper.INSTANCE.mapToInternalModel(tTemplate);

		tableViewer.getTableViewer().setInput(templateEntry);

		update();

		return templateEntry;
	}

	public TemplateEntry getTemplateEntry() {
		return templateEntry;
	}

}
