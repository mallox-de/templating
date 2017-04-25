package de.mallox.eclipse.templates.model.velocity;

/**
 * {@link VelocityFile} convenience class which wraps the filename and its
 * location.
 */
public class VelocityFile {

	/**
	 * File-Name of this {@link VelocityFile}.
	 */
	private String fileName;

	/**
	 * Workspace relative location of this {@link VelocityFile}.
	 */
	private String location;

	/**
	 * Absolute location of this {@link VelocityFile}.
	 */
	private String rawLocation;

	public VelocityFile(String pFileName, String pLocation, String pRowLocation) {
		super();
		fileName = pFileName;
		location = pLocation;
		rawLocation = pRowLocation;
	}

	public String getFileName() {
		return fileName;
	}

	public String getLocation() {
		return location;
	}

	public String getRawLocation() {
		return rawLocation;
	}

	/**
	 * Get project relative path of this {@link VelocityFile}.
	 * 
	 * @return
	 */
	public String getProjectRelativeLocation() {
		return getLocation().replaceFirst("^\\/[^\\/]+", "");
	}

	/**
	 * Get project-name of the location.
	 * 
	 * @return
	 */
	public String getProjectName() {
		return getFile().split("/")[1];
	}

	/**
	 * Get workspace relative path of this {@link VelocityFile}; using
	 * {@link #getLocation()}.
	 * 
	 * @return
	 */
	public String getFile() {
		return getLocation() + "/" + getFileName();
	}

	/**
	 * Get full path of this {@link VelocityFile}; using
	 * {@link #getRowLocation()}.
	 * 
	 * @return
	 */
	public String getRawFile() {
		return getRawLocation() + "/" + getFileName();
	}

	/**
	 * Get project relative path of this {@link VelocityFile}.
	 * 
	 * @return
	 */
	public String getProjectRelativeFile() {
		return getProjectRelativeLocation() + "/" + getFileName();
	}
}
