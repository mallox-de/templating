package de.mallox.eclipse.templates.widgets.variables;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import de.mallox.eclipse.templates.model.VariableEntry;
import de.mallox.eclipse.templates.wizards.templates.ConfigureTemplateWizardPage;

public class VariablesTableViewer {
	private TableViewer tableViewer;
	private Table table;
	private ConfigureTemplateWizardPage wizardPage;

	public enum ColumnNames {
		Type("Typ"), Name("Name"), Value("Wert"), Description("Beschreibung");

		private String value;

		private static Map<String, ColumnNames> lookupMap;

		private ColumnNames(String pValue) {
			this.value = pValue;
		}

		synchronized private static void init() {
			if (lookupMap == null) {
				lookupMap = new HashMap<>();

				ColumnNames[] values = values();
				for (int i = 0; i < values.length; i++) {
					lookupMap.put(values[i].getValue(), values[i]);
				}
			}
		}

		public static synchronized ColumnNames lookup(String pValue) {
			if (lookupMap == null) {
				init();
			}
			return lookupMap.get(pValue);
		}

		public static String[] getColumnNames() {
			ColumnNames[] values = values();
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

	public VariablesTableViewer(Composite composite, ConfigureTemplateWizardPage pWizardPage) {
		wizardPage = pWizardPage;
		addChildControls(composite);
	}

	/**
	 * Create a new shell, add the widgets, open the shell
	 * 
	 * @return the shell that was created
	 */
	private void addChildControls(Composite composite) {

		// Create the table
		createTable(composite);

		// Create and setup the TableViewer
		createTableViewer();
		tableViewer.setContentProvider(new VariablesContentProvider());
		tableViewer.setLabelProvider(new VariablesLabelProvider());

	}

	public TableViewer getTableViewer() {
		return tableViewer;
	}

	/**
	 * Create the Table
	 */
	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		table = new Table(parent, style);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText(ColumnNames.Type.getValue());
		column.setWidth(60);

		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(ColumnNames.Name.getValue());
		column.setWidth(200);

		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(ColumnNames.Value.getValue());
		column.setWidth(200);

		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText(ColumnNames.Description.getValue());
		column.setWidth(200);

	}

	/**
	 * Create the TableViewer
	 */
	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);
		tableViewer.setColumnProperties(ColumnNames.getColumnNames());

		// Create the cell editors
		final CellEditor[] editors = new CellEditor[ColumnNames.getColumnNames().length];

		// Column 2 : Description (Free text)
		TextCellEditor textEditor = new TextCellEditor(table);
		((Text) textEditor.getControl()).setTextLimit(60);
		editors[2] = textEditor;
		
		// Assign the cell editors to the viewer
		tableViewer.setCellEditors(editors);
		// Set the cell modifier for the viewer
		tableViewer.setCellModifier(new VariablesCellModifier(this, table, editors, wizardPage));
		// Set the default sorter for the viewer
		// tableViewer.setSorter(new
		// ExampleTaskSorter(ExampleTaskSorter.DESCRIPTION));
	}

	public void valueChanged(VariableEntry variable) {
		tableViewer.update(variable, null);
		wizardPage.update();
	}

}
