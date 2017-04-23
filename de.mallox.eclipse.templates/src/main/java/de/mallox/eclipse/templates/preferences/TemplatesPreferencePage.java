package de.mallox.eclipse.templates.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.mallox.eclipse.templates.provider.TemplatePreferenceProvider;
import de.mallox.eclipse.templates.widgets.preferences.PreferenceTableViewer;

public class TemplatesPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Composite container;
	private TemplatePreferenceDataList prefDataList;
	private TemplatePreferenceProvider preferenceProvider = new TemplatePreferenceProvider(); 

	public TemplatesPreferencePage() {
	}

	public TemplatesPreferencePage(String pTitle) {
		super(pTitle);
	}

	public TemplatesPreferencePage(String pTitle, ImageDescriptor pImage) {
		super(pTitle, pImage);
	}

	@Override
	public void init(IWorkbench pIWorkbench) {
		// NOP
	}

	@Override
	protected Control createContents(Composite pParent) {
		container = new Composite(pParent, SWT.NONE);
		GridLayout tGridLayout = new GridLayout();
		tGridLayout.numColumns = 1;
		tGridLayout.makeColumnsEqualWidth = true;
		container.setLayout(tGridLayout);

		PreferenceTableViewer tPreferenceTableViewer = new PreferenceTableViewer(container, this);

		
		prefDataList = preferenceProvider.readPreference();

		tPreferenceTableViewer.setPrefDataList(prefDataList);

		return container;
	}



	/**
	 * Method declared on IPreferencePage. Save the given preferences to the
	 * preference store.
	 *
	 * @return [boolean] true if saving was successful
	 *
	 * @.author matysiak
	 *
	 * @.threadsafe no
	 *
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#performOk()
	 */
	public boolean performOk() {
		preferenceProvider.saveTableToPrefs(this.prefDataList);
		return super.performOk();
	}

}
