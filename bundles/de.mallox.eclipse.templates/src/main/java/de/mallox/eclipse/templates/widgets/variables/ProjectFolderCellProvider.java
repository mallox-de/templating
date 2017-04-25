package de.mallox.eclipse.templates.widgets.variables;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

public class ProjectFolderCellProvider extends DialogCellEditor{
	
	IContainer container = null;

	public ProjectFolderCellProvider(Composite parent) {
		super(parent);
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		String tPath = (String) getValue();
		IPath tIPath = new Path(tPath == null ? "/." : tPath);		
		IResource iResource = ResourcesPlugin.getWorkspace().getRoot().findMember(tIPath);
		if (iResource != null && iResource.exists() && iResource.isAccessible() && iResource instanceof IContainer) {
			container = (IContainer) iResource;
		}
		
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(cellEditorWindow.getShell(), container, true, "Select a folder:");

		dialog.setTitle("Destination Selection");

		int open = dialog.open();
		
		if (open == Window.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1 && result[0] instanceof Path) {
				Path tNewPath = (Path) result[0];
				
				tPath = tNewPath.toString();
			}
		}
		
		return tPath;
	}

}
