//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.02.22 um 12:18:20 AM CET 
//

package de.mallox.eclipse.templates.model;

import java.util.ArrayList;
import java.util.List;

import de.mallox.eclipse.templates.model.template.ProjectTemplate;

/**
 * Ein Template kann ein oder mehrere Projekte umfassen.
 */
public class TemplateEntry {

	private String description;
	private List<VariableEntry> variable;
	private List<VariableCombinationEntry> variableCombinations;
	private List<ProjectTemplateEntry> projectTemplate;
	private List<VelocityTool> velocityTools;
	private String name;
	private String version;
	private String generatorVersion;

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
	 * Gets the value of the variable property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the variable property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getVariable().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Variable
	 * }
	 * 
	 * 
	 */
	public List<VariableEntry> getVariable() {
		if (variable == null) {
			variable = new ArrayList<VariableEntry>();
		}
		return this.variable;
	}

	/**
	 * Gets the value of the variableCombinations property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the variableCombinations property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getVariableCombinations().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link VariableCombinationEntry }
	 * 
	 * 
	 */
	public List<VariableCombinationEntry> getVariableCombinations() {
		if (variableCombinations == null) {
			variableCombinations = new ArrayList<VariableCombinationEntry>();
		}
		return this.variableCombinations;
	}

	/**
	 * Gets the value of the projectTemplate property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the projectTemplate property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getProjectTemplate().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list
	 * {@link ProjectTemplate }
	 * 
	 * 
	 */
	public List<ProjectTemplateEntry> getProjectTemplate() {
		if (projectTemplate == null) {
			projectTemplate = new ArrayList<ProjectTemplateEntry>();
		}
		return this.projectTemplate;
	}

	/**
	 * Gets the value of the velocityTools property.
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
	 * getVelocityTools().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
	public List<VelocityTool> getVelocityTools() {
		if (velocityTools == null) {
			velocityTools = new ArrayList<VelocityTool>();
		}
		return this.velocityTools;
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
	 * Ruft den Wert der version-Eigenschaft ab.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Legt den Wert der version-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setVersion(String value) {
		this.version = value;
	}

	/**
	 * Ruft den Wert der generatorVersion-Eigenschaft ab.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getGeneratorVersion() {
		return generatorVersion;
	}

	/**
	 * Legt den Wert der generatorVersion-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setGeneratorVersion(String value) {
		this.generatorVersion = value;
	}
}
