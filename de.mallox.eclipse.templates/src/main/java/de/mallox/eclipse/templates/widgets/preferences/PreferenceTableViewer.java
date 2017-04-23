package de.mallox.eclipse.templates.widgets.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import de.mallox.eclipse.templates.preferences.ITemplatePreferenceDataListViewer;
import de.mallox.eclipse.templates.preferences.TemplatePreferenceData;
import de.mallox.eclipse.templates.preferences.TemplatePreferenceDataList;

public class PreferenceTableViewer implements ITemplatePreferenceDataListViewer {
	private TableViewer tableViewer;
	private Table table;

	private TemplatePreferenceDataList prefDataList = new TemplatePreferenceDataList();
	private PreferencePage preferencePage;

	public enum ColumnName {
		Name("Name"), BaseUrl("BaseURL"), DescriptionFile("Description-File");

		private String value;

		private static Map<String, ColumnName> lookupMap;

		private ColumnName(String pValue) {
			this.value = pValue;
		}

		synchronized private static void init() {
			if (lookupMap == null) {
				lookupMap = new HashMap<>();

				ColumnName[] values = values();
				for (int i = 0; i < values.length; i++) {
					lookupMap.put(values[i].getValue(), values[i]);
				}
			}
		}

		public static synchronized ColumnName lookup(String pValue) {
			if (lookupMap == null) {
				init();
			}
			return lookupMap.get(pValue);
		}

		public static String[] getColumnNames() {
			ColumnName[] values = values();
			String[] tColumnNames = new String[values.length];
			for (int i = 0; i < values.length; i++) {
				tColumnNames[i] = values[i].getValue();
			}
			return tColumnNames;
		}

		public String getValue() {
			return value;
		}
	}

	public PreferenceTableViewer(Composite pComposite, PreferencePage pPreferencePage) {
		preferencePage = pPreferencePage;
		addChildControls(pComposite);
	}

	public void setPrefDataList(TemplatePreferenceDataList pPrefDataList) {
		prefDataList = pPrefDataList;
		getTableViewer().setInput(pPrefDataList);
		pPrefDataList.addChangeListener(this);
	}

	/**
	 * Create a new shell, add the widgets, open the shell
	 * 
	 * @return the shell that was created
	 */
	private void addChildControls(Composite pComposite) {

		// Create the table
		createTable(pComposite);

		// Create and setup the TableViewer
		createTableViewer();
		tableViewer.setContentProvider(new PreferenceContentProvider());
		tableViewer.setLabelProvider(new PreferenceLabelProvider());

		// Add the buttons
		createButtons(pComposite);
	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	/**
	 * Create the Table
	 */
	private void createTable(Composite pParent) {
		int tStyle = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		table = new Table(pParent, tStyle);

		GridData tGridData = new GridData(GridData.FILL_BOTH);
		tGridData.grabExcessVerticalSpace = true;
		tGridData.horizontalSpan = 3;
		table.setLayoutData(tGridData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// 1st column:
		TableColumn tTableColumn = new TableColumn(table, SWT.LEFT, 0);
		tTableColumn.setText(ColumnName.Name.getValue());
		tTableColumn.setWidth(200);

		// 2nd column:
		tTableColumn = new TableColumn(table, SWT.LEFT, 1);
		tTableColumn.setText(ColumnName.BaseUrl.getValue());
		tTableColumn.setWidth(200);

		// 3rd column:
		tTableColumn = new TableColumn(table, SWT.LEFT, 2);
		tTableColumn.setText(ColumnName.DescriptionFile.getValue());
		tTableColumn.setWidth(200);
	}

	/**
	 * Create the TableViewer
	 */
	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(ColumnName.getColumnNames());

		// Create the cell editors
		final CellEditor[] tCellEditors = new CellEditor[ColumnName.getColumnNames().length];

		// Column 1 :
		TextCellEditor tTextCellEditor = new TextCellEditor(table);
		((Text) tTextCellEditor.getControl()).setTextLimit(60);
		tCellEditors[0] = tTextCellEditor;

		// Column 2 :
		tTextCellEditor = new TextCellEditor(table);
		((Text) tTextCellEditor.getControl()).setTextLimit(256);
		tCellEditors[1] = tTextCellEditor;
		tCellEditors[1].setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object pValue) {
				if (pValue instanceof String) {
					String tValue = (String) pValue;

					if (!tValue.endsWith("/") && !tValue.endsWith("\\")) {
						return ColumnName.BaseUrl.getValue() + " must end with '/' or '\\'";
					}
				}
				return null;
			}
		});
		tCellEditors[1].addListener(new ICellEditorListener() {
			@Override
			public void editorValueChanged(boolean pOldValidState, boolean pNewValidState) {
				preferencePage.setErrorMessage(tCellEditors[1].getErrorMessage());
			}
			@Override
			public void cancelEditor() {
				preferencePage.setErrorMessage(null);
			}
			@Override
			public void applyEditorValue() {
				preferencePage.setErrorMessage(null);
			}
		});
		
		// Column 3 :
		tTextCellEditor = new TextCellEditor(table);
		((Text) tTextCellEditor.getControl()).setTextLimit(60);
		tCellEditors[2] = tTextCellEditor;

		// Assign the cell editors to the viewer
		tableViewer.setCellEditors(tCellEditors);
		// Set the cell modifier for the viewer
		tableViewer.setCellModifier(new PreferenceCellModifier(this));
	}

	/**
	 * Add the "Add", "Delete" and "Close" buttons
	 * 
	 * @param pParent
	 *            the parent composite
	 */
	private void createButtons(Composite pParent) {
		Composite tContainer = new Composite(pParent, SWT.NONE);
		GridLayout tGridLayout = new GridLayout();
		tGridLayout.numColumns = 2;
		tGridLayout.makeColumnsEqualWidth = true;
		tContainer.setLayout(tGridLayout);

		// Create and configure the "Add" button
		Button tButtonAdd = new Button(tContainer, SWT.PUSH | SWT.CENTER);
		tButtonAdd.setText("Add");

		GridData tGridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		tGridData.widthHint = 80;
		tButtonAdd.setLayoutData(tGridData);
		tButtonAdd.addSelectionListener(new SelectionAdapter() {

			// Add a task to the ExampleTaskList and refresh the view
			public void widgetSelected(SelectionEvent e) {
				prefDataList.addTask();
			}
		});

		// Create and configure the "Delete" button
		Button tButtonDelete = new Button(tContainer, SWT.PUSH | SWT.CENTER);
		tButtonDelete.setText("Delete");
		tGridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		tGridData.widthHint = 80;
		tButtonDelete.setLayoutData(tGridData);

		tButtonDelete.addSelectionListener(new SelectionAdapter() {

			// Remove the selection and refresh the view
			public void widgetSelected(SelectionEvent pSelectionEvent) {
				TemplatePreferenceData tTemplatePreferenceData = (TemplatePreferenceData) ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				if (tTemplatePreferenceData != null) {
					prefDataList.removeTask(tTemplatePreferenceData);
				}
			}
		});

	}

	@Override
	public void addTask(TemplatePreferenceData pPrefData) {
		tableViewer.add(pPrefData);
	}

	@Override
	public void removeTask(TemplatePreferenceData pPrefData) {
		tableViewer.remove(pPrefData);
	}

	@Override
	public void updateTask(TemplatePreferenceData pPrefData) {
		tableViewer.update(pPrefData, null);

	}

}
