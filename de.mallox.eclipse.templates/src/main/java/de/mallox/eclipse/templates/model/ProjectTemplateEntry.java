//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.02.22 um 12:18:20 AM CET 
//

package de.mallox.eclipse.templates.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Beschreibt ein Projekt, welches im Rahmen des Templates angelegt werden soll.
 */
public class ProjectTemplateEntry {

	private String description;
	private ProjectDescriptionEntry projectDescription;
	private List<ClasspathentryEntry> classpathentries;
	private List<FileEntry> files;
	private List<String> launcher;
	private List<String> resolvedLauncher;
	private String condition;
	private String conditionValue;
    private Boolean alreadyExists;
	private String name;
	private String resolvedName;
	private String resolvedCondition;
    private Boolean compileWorkspace;
    private String encoding;
	
	public String getResolvedName() {
		return resolvedName;
	}

	public void setResolvedName(String pResolvedName) {
		resolvedName = pResolvedName;
	}

	/**
	 * Ruft den Wert der description-Eigenschaft ab.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Legt den Wert der description-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDescription(String value) {
		this.description = value;
	}

	/**
	 * Ruft den Wert der projectDescription-Eigenschaft ab.
	 * 
	 * @return possible object is {@link ProjectDescriptionEntry }
	 * 
	 */
	public ProjectDescriptionEntry getProjectDescription() {
		return projectDescription;
	}

	/**
	 * Legt den Wert der projectDescription-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link ProjectDescriptionEntry }
	 * 
	 */
	public void setProjectDescription(ProjectDescriptionEntry value) {
		this.projectDescription = value;
	}

	/**
	 * Gets the value of the classpathentries property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the classpathentries property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getClasspathentries().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ClasspathentryEntry }
	 * 
	 * 
	 */
	public List<ClasspathentryEntry> getClasspathentries() {
		if (classpathentries == null) {
			classpathentries = new ArrayList<ClasspathentryEntry>();
		}
		return this.classpathentries;
	}

	/**
	 * Gets the value of the files property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the files property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getFiles().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link FileEntry
	 * }
	 * 
	 * 
	 */
	public List<FileEntry> getFiles() {
		if (files == null) {
			files = new ArrayList<FileEntry>();
		}
		return this.files;
	}

	/**
	 * Gets the value of the launcher property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the launcher property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getLauncher().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
	public List<String> getLauncher() {
		if (launcher == null) {
			launcher = new ArrayList<String>();
		}
		return this.launcher;
	}

	/**
	 * Gets the value of the resolvedLauncher property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the launcher property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getResolvedLauncher().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
	public List<String> getResolvedLauncher() {
		if (resolvedLauncher == null) {
			resolvedLauncher = new ArrayList<String>();
		}
		return this.resolvedLauncher;
	}
	
	/**
	 * Ruft den Wert der name-Eigenschaft ab.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * Legt den Wert der name-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setName(String value) {
		this.name = value;
	}

	/**
	 * Ruft den Wert der condition-Eigenschaft ab.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * Legt den Wert der condition-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCondition(String value) {
		this.condition = value;
	}

	/**
	 * Ruft den Wert der conditionValue-Eigenschaft ab.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getConditionValue() {
		return conditionValue;
	}

	/**
	 * Legt den Wert der conditionValue-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setConditionValue(String value) {
		this.conditionValue = value;
	}

	public String getResolvedCondition() {
		return resolvedCondition;
	}

	public void setResolvedCondition(String pResolvedCondition) {
		resolvedCondition = pResolvedCondition;
	}
	
	   /**
     * Ruft den Wert der alreadyExists-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAlreadyExists() {
        return alreadyExists;
    }

    /**
     * Legt den Wert der alreadyExists-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAlreadyExists(Boolean value) {
        this.alreadyExists = value;
    }
    
    /**
     * Ruft den Wert der compileWorkspace-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isCompileWorkspace() {
        return compileWorkspace;
    }

    /**
     * Legt den Wert der compileWorkspace-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCompileWorkspace(Boolean value) {
        this.compileWorkspace = value;
    }

    /**
     * Ruft den Wert der encoding-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
	public String getEncoding() {
		return encoding;
	}

    /**
     * Legt den Wert der encoding-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
 
    
}
