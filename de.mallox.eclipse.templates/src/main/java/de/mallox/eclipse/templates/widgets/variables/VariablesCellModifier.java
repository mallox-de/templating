package de.mallox.eclipse.templates.widgets.variables;

import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import de.mallox.eclipse.templates.model.VariableEntry;
import de.mallox.eclipse.templates.model.VariableType;
import de.mallox.eclipse.templates.widgets.variables.VariablesTableViewer.ColumnNames;

public class VariablesCellModifier implements ICellModifier {

	private VariablesTableViewer tableViewer;
	private CellEditor[] editors;
	private Table table;
	private DialogPage wizardPage;

	/**
	 * 
	 * @param tableViewer
	 * @param pTable
	 * @param pEditors
	 *            hack to update {@link CellEditor} depending on type to edit.
	 * @param pWizardPage
	 */
	public VariablesCellModifier(VariablesTableViewer tableViewer, Table pTable, CellEditor[] pEditors,
			DialogPage pWizardPage) {
		super();
		this.tableViewer = tableViewer;
		table = pTable;
		editors = pEditors;
		wizardPage = pWizardPage;
	}

	@Override
	public boolean canModify(Object element, String property) {

		if (element instanceof VariableEntry) {
			VariableEntry tVariableEntry = (VariableEntry) element;

			editors[2] = getCellEditor(tVariableEntry.getType());

			return true;
		}

		return false;
	}

	private CellEditor getCellEditor(String pType) {

		// TODO cache editors...
		final CellEditor[] tCellEditor = new CellEditor[1];
		if (VariableType.BOOLEAN.equals(pType)) {
			tCellEditor[0] = new CheckboxCellEditor(table);
		} else if (VariableType.INT.equals(pType)) {
			tCellEditor[0] = new IntegerCellEditor(table);
		} else if (VariableType.PROJECT_FOLDER.equals(pType)) {
			tCellEditor[0] = new ProjectFolderCellProvider(table);
		} else if (VariableType.PROJECT_FILE.equals(pType)) {
			tCellEditor[0] = new ProjectFileCellProvider(table);
		} else if (VariableType.PROJECT_FILE_ARRAY.equals(pType)) {
			tCellEditor[0] = new ProjectFileArrayCellProvider(table);
		} else {
			tCellEditor[0] = new TextCellEditor(table);
		}

		tCellEditor[0].addListener(new ICellEditorListener() {
			@Override
			public void editorValueChanged(boolean pOldValidState, boolean pNewValidState) {
				wizardPage.setErrorMessage(tCellEditor[0].getErrorMessage());
			}

			@Override
			public void cancelEditor() {
				wizardPage.setErrorMessage(null);
			}

			@Override
			public void applyEditorValue() {
				wizardPage.setErrorMessage(null);
			}
		});

		return tCellEditor[0];
	}

	@Override
	public Object getValue(Object element, String property) {

		if (element instanceof VariableEntry) {
			VariableEntry variable = (VariableEntry) element;
			String tType = variable.getType();

			ColumnNames columnName = VariablesTableViewer.ColumnNames.lookup(property);

			switch (columnName) {
			case Value:
				if ("boolean".equals(tType)) {
					return Boolean.valueOf(variable.getValue());
				} else if ("int".equals(tType)) {
					return Integer.valueOf(variable.getValue());
				}
				return variable.getValue();
			default:
				break;
			}
		}
		return "";
	}

	@Override
	public void modify(Object element, String property, Object value) {
		if (element instanceof TableItem) {
			TableItem tableItem = (TableItem) element;

			if (tableItem != null && tableItem.getData() instanceof VariableEntry) {
				VariableEntry variable = (VariableEntry) tableItem.getData();

				ColumnNames columnName = VariablesTableViewer.ColumnNames.lookup(property);

				switch (columnName) {
				case Value:
					/*
					 * If value is not valid null will be returned and ignored:
					 */
					if (value != null) {
						variable.setValue(String.valueOf(value));
					}
					tableViewer.valueChanged(variable);
					break;
				default:
					break;
				}
			}
		}

	}

}
