package de.mallox.eclipse.templates.widgets.variables;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;

import de.mallox.eclipse.templates.util.CSVConverter;

public class ProjectFileArrayCellProvider extends DialogCellEditor {

	private IContainer folder = null;

	public ProjectFileArrayCellProvider(Composite parent) {
		super(parent);
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		String[] tValues = CSVConverter.parseStringArray((String) getValue());

		String tFile = null;
		if (tValues != null && tValues.length != 0) {
			tFile = tValues[0];
		}

		if (tFile != null && tFile.length() != 0) {
			IResource tOldFileResource = ResourcesPlugin.getWorkspace().getRoot().findMember(tFile);
			if (tOldFileResource != null && tOldFileResource.exists() && tOldFileResource.isAccessible()
					&& tOldFileResource instanceof IContainer) {
				folder = (IContainer) tOldFileResource;
			}
		}

		FileDialog dialog = new FileDialog(cellEditorWindow.getShell(), SWT.OPEN | SWT.MULTI);

		dialog.setText("Select filenames");
		dialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toFile().toString());

		if (folder != null) {
			if (folder instanceof Project) {
				Project tProject = (Project) folder;
				dialog.setFilterPath(tProject.getFullPath().toFile().toString());
			} else {
				dialog.setFilterPath(folder.getRawLocation().toFile().toString());
			}
		} else {
			dialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot().getRawLocation().toFile().toString());
		}

		String tNewFile = dialog.open();

		if (tNewFile != null && tNewFile.length() != 0) {
			IResource tNewFileResource = ResourcesPlugin.getWorkspace().getRoot()
					.getFileForLocation(new Path(tNewFile));
			if (tNewFileResource != null && tNewFileResource.exists() && tNewFileResource.isAccessible()
					&& tNewFileResource instanceof IFile) {
				folder = ((IFile) tNewFileResource).getParent();
				tFile = folder.getFullPath().toFile().toString().replace('\\', '/');
			}

			String[] tNewFileNames = dialog.getFileNames();

			if (tNewFileNames == null || tNewFileNames.length == 0) {
				return "";
			}

			String[] tNewValues = new String[tNewFileNames.length + 1];

			tNewValues[0] = tFile;
			System.arraycopy(tNewFileNames, 0, tNewValues, 1, tNewFileNames.length);

			return CSVConverter.convertToString(tNewValues);
		}

		return "";
	}

}
