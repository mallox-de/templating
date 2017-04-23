package de.mallox.eclipse.templates.widgets.variables;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.mallox.eclipse.templates.model.VariableEntry;

public class VariablesLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof VariableEntry) {
			VariableEntry variable = (VariableEntry) element;
			
			switch (columnIndex) {
			case 0:
				return variable.getType();
			case 1:
				return variable.getName();
			case 2:
				return variable.getValue();
			case 3:
				return variable.getDescription();
			}
			
			return "";
		}
		return null;
	}

}
