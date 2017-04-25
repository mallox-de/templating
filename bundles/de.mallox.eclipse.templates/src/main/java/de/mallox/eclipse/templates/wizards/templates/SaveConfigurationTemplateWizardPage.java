package de.mallox.eclipse.templates.wizards.templates;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import de.mallox.eclipse.templates.model.TemplateRefEntry;

public class SaveConfigurationTemplateWizardPage extends WizardPage implements IWizardPage {

	private Composite container;
	private TemplateRefEntry templateRef;
	private Button saveConfigurationButton;
	private Text filePathText;
	private Button browseDestinationButton;
	private Group locationGroup;
	private Text fileNameText;
	private Path newPath;

	public SaveConfigurationTemplateWizardPage() {
		super("Templates");
		setTitle("Templates");
		setDescription("Save configuration.");
	}

	@Override
	public void createControl(Composite parent) {
		// setup composite:
		container = new Composite(parent, SWT.NONE);
		GridLayout tLayout = new GridLayout();
		tLayout.numColumns = 1;
		tLayout.makeColumnsEqualWidth = false;
		container.setLayout(tLayout);

		// add save configuration check-box:
		saveConfigurationButton = new Button(container, SWT.CHECK);
		saveConfigurationButton.setText("Save as Template-Launcher");
		saveConfigurationButton.setSelection(false);
		saveConfigurationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pE) {
				boolean tSelection = saveConfigurationButton.getSelection();
				if (tSelection) {
					fileNameText.setText(templateRef.getName() + ".tconf");
				}
				setEnabledRecursive(locationGroup, tSelection);
				update();
			}
		});
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		saveConfigurationButton.setLayoutData(gridData);

		// add group:
		locationGroup = new Group(container, SWT.NULL);
		locationGroup.setText("Location:");
		locationGroup.setEnabled(false);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		locationGroup.setLayoutData(gridData);

		tLayout = new GridLayout();
		tLayout.numColumns = 3;
		tLayout.makeColumnsEqualWidth = false;
		locationGroup.setLayout(tLayout);

		// filename:
		new Label(locationGroup, SWT.NONE).setText("Filename:");
		fileNameText = new Text(locationGroup, SWT.BORDER);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 2;
		fileNameText.setLayoutData(gridData);
		fileNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent pE) {
				update();
			}
		});

		// file-path:
		new Label(locationGroup, SWT.NONE).setText("Path:");
		filePathText = new Text(locationGroup, SWT.BORDER);
		filePathText.setEnabled(false);
		filePathText.setEditable(false);
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		filePathText.setLayoutData(gridData);
		filePathText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent pE) {
				update();
			}
		});

		browseDestinationButton = new Button(locationGroup, SWT.PUSH);
		browseDestinationButton.setText("Browse...");
		browseDestinationButton.setEnabled(false);
		browseDestinationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pE) {
				createDialog();
				update();
			}
		});

		setEnabledRecursive(locationGroup, false);

		// required to avoid an error in the system
		setControl(container);
		update();
	}

	public void init(TemplateRefEntry pTemplateRef) {
		templateRef = pTemplateRef;

		update();
	}

	private void createDialog() {
		String tPath = filePathText.getText();
		if (tPath == null || tPath.length() == 0) {
			tPath = "/.";
		}
		IResource iResource = ResourcesPlugin.getWorkspace().getRoot().findMember(tPath);
		IContainer tContainer = null;
		if (iResource != null && iResource.exists() && iResource.isAccessible() && iResource instanceof IContainer) {
			tContainer = (IContainer) iResource;
		}

		ContainerSelectionDialog dialog = new ContainerSelectionDialog(container.getShell(), tContainer, true,
				"Select a folder:");

		dialog.setTitle("Destination Selection");

		int open = dialog.open();

		if (open == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1 && result[0] instanceof Path) {
				newPath = (Path) result[0];

				filePathText.setText(newPath.toString());
			}
		}
	}

	public static void setEnabledRecursive(final Composite composite, final boolean enabled) {
		if (composite == null) {
			return;
		}

		Control[] children = composite.getChildren();

		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Composite) {
				setEnabledRecursive((Composite) children[i], enabled);
			} else {
				children[i].setEnabled(enabled);
			}
		}

		composite.setEnabled(enabled);
	}

	public void update() {
		if (saveConfiguration()) {
			if (fileNameText.getText() == null || fileNameText.getText().length() == 0) {
				setMessage("Filename ist empty.");
				setPageComplete(false);
				return;
			}
			if (!fileNameText.getText().endsWith(".tconf")) {
				setMessage("Filename must end with '.tconf'.");
				setPageComplete(false);
				return;
			}
			if (filePathText.getText() == null || filePathText.getText().length() == 0) {
				setMessage("File-Path ist empty.");
				setPageComplete(false);
				return;
			}
		}
		setMessage(null);
		setPageComplete(true);
	}

	public boolean saveConfiguration() {
		return saveConfigurationButton.getSelection();
	}

	public String getConfigurationFile() {
		return filePathText.getText() + "/" + fileNameText.getText();
	}

}
