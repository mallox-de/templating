package de.mallox.eclipse.templates.widgets.variables;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;

public class ProjectFileCellProvider extends DialogCellEditor {

	private IFile file = null;

	public ProjectFileCellProvider(Composite parent) {
		super(parent);
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		String tFile = (String) getValue();

		if (tFile != null && tFile.length() != 0) {
			IResource tOldFileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(tFile);
			if (tOldFileResource != null && tOldFileResource.exists() && tOldFileResource.isAccessible()
					&& tOldFileResource instanceof IFile) {
				file = (IFile) tOldFileResource;
			}
		}

		FileDialog dialog = new FileDialog(cellEditorWindow.getShell(), SWT.OPEN);

		dialog.setText("Select file");

		if (file != null) {
			dialog.setFilterPath(file.getRawLocation().toFile().toString());
			dialog.setFileName(file.getName());
		} else {
			dialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toFile().toString());
		}

		String tNewFile = dialog.open();

		if (tNewFile != null && tNewFile.length() != 0) {
			IResource tNewFileResource = ResourcesPlugin.getWorkspace().getRoot()
					.getFileForLocation(new Path(tNewFile));
			if (tNewFileResource != null && tNewFileResource.exists() && tNewFileResource.isAccessible()
					&& tNewFileResource instanceof IFile) {
				file = (IFile) tNewFileResource;
				tFile = file.getFullPath().toFile().toString().replace('\\', '/');
			}
		}

		return tFile;
	}

}
