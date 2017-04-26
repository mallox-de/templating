package de.mallox.eclipse.templates.wizards.create;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.filters.EmptyInnerPackageFilter;
import org.eclipse.jdt.internal.ui.jarpackager.CheckboxTreeAndListGroup;
import org.eclipse.jdt.internal.ui.jarpackager.JarPackagerMessages;
import org.eclipse.jdt.internal.ui.util.SWTUtil;
import org.eclipse.jdt.ui.JavaElementComparator;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.ProblemsLabelDecorator;
import org.eclipse.jdt.ui.StandardJavaElementContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

// org.eclipse.jdt.internal.ui.jarpackager.JarPackageWizardPage
// org.eclipse.jdt.internal.ui.jarpackager.AbstractJarDestinationWizardPage
public class SelectProjectResourcesWizardPage extends WizardPage implements IWizardPage, Listener {

	// constants
	private static final int SIZING_SELECTION_WIDGET_WIDTH = 480;
	private static final int SIZING_SELECTION_WIDGET_HEIGHT = 150;
	protected static final int SIZING_TEXT_FIELD_WIDTH = 250;

	private Composite container;
	private CheckboxTreeAndListGroup inputGroup;

	private Text destinationNameText;
	private Button destinationBrowseButton;
	private IPath destinationPath;

	public SelectProjectResourcesWizardPage() {
		super("Create template");
		setTitle("Create template from selekted source");
		setDescription("Select resouces for new template.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(Composite pParent) {
		container = new Composite(pParent, SWT.NONE);
		GridLayout tLayout = new GridLayout();
		tLayout.numColumns = 1;
		tLayout.makeColumnsEqualWidth = true;
		container.setLayout(tLayout);

		createInputGroup(container);

		createDestinationGroup(container);

		// required to avoid an error in the system
		setControl(container);
		setPageComplete(false);
	}

	/**
	 * Creates the checkbox tree and list for selecting resources.
	 *
	 * @param pParent
	 *            the parent control
	 */
	protected void createInputGroup(Composite pParent) {
		int tLabelFlags = JavaElementLabelProvider.SHOW_BASICS | JavaElementLabelProvider.SHOW_OVERLAY_ICONS
				| JavaElementLabelProvider.SHOW_SMALL_ICONS;
		ITreeContentProvider tTreeContentProvider = new StandardJavaElementContentProvider() {
			@Override
			public boolean hasChildren(Object pElement) {
				// prevent the + from being shown in front of packages
				return !(pElement instanceof IPackageFragment) && super.hasChildren(pElement);
			}
		};
		final DecoratingLabelProvider tLabelProvider = new DecoratingLabelProvider(
				new JavaElementLabelProvider(tLabelFlags), new ProblemsLabelDecorator(null));
		inputGroup = new CheckboxTreeAndListGroup(pParent, JavaCore.create(ResourcesPlugin.getWorkspace().getRoot()),
				tTreeContentProvider, tLabelProvider, new StandardJavaElementContentProvider(), tLabelProvider,
				SWT.NONE, SIZING_SELECTION_WIDGET_WIDTH, SIZING_SELECTION_WIDGET_HEIGHT) {

//			@Override
//			protected void setTreeChecked(final Object pElement, final boolean pState) {
//				if (pElement instanceof IResource) {
//					final IResource tResource = (IResource) pElement;
//					if (tResource.getName().charAt(0) == '.')
//						return;
//				}
//				super.setTreeChecked(pElement, pState);
//			}
		};
		inputGroup.addTreeFilter(new EmptyInnerPackageFilter());
		inputGroup.setTreeComparator(new JavaElementComparator());
		inputGroup.setListComparator(new JavaElementComparator());
		inputGroup.addTreeFilter(new ContainerFilter(ContainerFilter.FILTER_NON_CONTAINERS));
		inputGroup.addTreeFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer pViewer, Object pParentElement, Object pElement) {
				if (pElement instanceof IPackageFragmentRoot) {
					IPackageFragmentRoot tRoot = (IPackageFragmentRoot) pElement;
					return !tRoot.isArchive() && !tRoot.isExternal();
				}
				return true;
			}
		});
		inputGroup.addListFilter(new ContainerFilter(ContainerFilter.FILTER_CONTAINERS));
		inputGroup.getTree().addListener(SWT.MouseUp, this);
		inputGroup.getTable().addListener(SWT.MouseUp, this);

		SWTUtil.setAccessibilityText(inputGroup.getTree(),
				JarPackagerMessages.JarPackageWizardPage_tree_accessibility_message);
		SWTUtil.setAccessibilityText(inputGroup.getTable(),
				JarPackagerMessages.JarPackageWizardPage_table_accessibility_message);

		ICheckStateListener tListener = new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent pEvent) {
				updatePageCompletion();
			}
		};

		inputGroup.addCheckStateListener(tListener);
	}

	/*
	 * Overrides method from WizardExportPage
	 */
	protected void createDestinationGroup(Composite pParent) {

		initializeDialogUnits(pParent);

		// destination specification group
		Composite tDestinationSelectionGroup = new Composite(pParent, SWT.NONE);
		GridLayout tLayout = new GridLayout();
		tLayout.numColumns = 3;
		tDestinationSelectionGroup.setLayout(tLayout);
		tDestinationSelectionGroup
				.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));

		new Label(tDestinationSelectionGroup, SWT.NONE).setText("Speichern unter...");

		// destination name entry field
		destinationNameText = new Text(tDestinationSelectionGroup, SWT.SINGLE | SWT.BORDER);
		destinationNameText.setEnabled(false);

		GridData tGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		tGridData.widthHint = SIZING_TEXT_FIELD_WIDTH;
		tGridData.horizontalSpan = 1;
		destinationNameText.setLayoutData(tGridData);

		// destination browse button
		destinationBrowseButton = new Button(tDestinationSelectionGroup, SWT.PUSH);
		destinationBrowseButton.setText(JarPackagerMessages.JarPackageWizardPage_browseButton_text);
		destinationBrowseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		SWTUtil.setButtonDimensionHint(destinationBrowseButton);
		destinationBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent pEvent) {
				handleDestinationBrowseButtonPressed();
			}
		});
	}

	/**
	 * Open an appropriate destination browser so that the user can specify a
	 * source to import from
	 */
	protected void handleDestinationBrowseButtonPressed() {
		ContainerSelectionDialog tDialog = new ContainerSelectionDialog(getContainer().getShell(),
				ResourcesPlugin.getWorkspace().getRoot(), true, "Ziel Verzeichnis f�r das Template");

		int tDialogReturnCode = tDialog.open();
		if (tDialogReturnCode == ContainerSelectionDialog.OK) {
			Object[] tDialogResult = tDialog.getResult();

			if (tDialogResult != null && tDialogResult.length == 1 && tDialogResult[0] instanceof IPath) {
				destinationPath = (IPath) tDialogResult[0];

				destinationNameText.setText(destinationPath.makeRelative().toString());
				updatePageCompletion();
			}

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPageComplete() {
		boolean tIsComplete = destinationPath != null;
		tIsComplete = inputGroup.getAllCheckedListItems().hasNext() && tIsComplete;
		if (tIsComplete)
			setErrorMessage(null);
		return tIsComplete;
	}

	protected void updatePageCompletion() {
		boolean tIsPageComplete = isPageComplete();
		setPageComplete(tIsPageComplete);
		if (tIsPageComplete)
			setErrorMessage(null);
	}

	/**
	 * Returns an iterator over this page's collection of currently-specified
	 * elements to be exported. This is the primary element selection facility
	 * accessor for subclasses.
	 *
	 * @return an iterator over the collection of elements currently selected
	 *         for export
	 */
	public List<IResource> getSelectedResourcesIterator() {
		List<IResource> tSelectedResources = new ArrayList<>();

		@SuppressWarnings("unchecked")
		Iterator<Object> tCheckedListItems = inputGroup.getAllCheckedListItems();
		while (tCheckedListItems.hasNext()) {
			Object tResource = tCheckedListItems.next();
			if (tResource instanceof IResource) {
				tSelectedResources.add((IResource) tResource);
			} else if (tResource instanceof IJavaElement) {
				IJavaElement tIJavaElement = (IJavaElement) tResource;
				IResource tJavaResource = tIJavaElement.getResource();
				if (tJavaResource != null) {
					tSelectedResources.add(tJavaResource);
				}
			} else {
				throw new RuntimeException("Selected resource is no IResource: " + tResource.getClass().getName());
			}
		}

		return tSelectedResources;
	}

	/**
	 * Returns the {@link IPath} where the Template should be written to.
	 * 
	 * @return {@link IPath} where the Template should be written to.
	 */
	public IPath getDestinationPath() {
		return destinationPath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleEvent(Event event) {
		if (getControl() == null)
			return;
		updatePageCompletion();
	}

}
