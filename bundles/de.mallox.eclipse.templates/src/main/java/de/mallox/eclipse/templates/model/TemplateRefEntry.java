//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.10.14 um 05:33:36 PM CEST 
//

package de.mallox.eclipse.templates.model;

/**
 * Beschreibt ein Template.
 */
public class TemplateRefEntry {

	protected String name;
	protected String baseurl;
	protected String file;

	private String description;
	private boolean generatorVersionCompatibleWithTemplate;

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
	 * Ruft den Wert der baseurl-Eigenschaft ab.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBaseurl() {
		return baseurl;
	}

	/**
	 * Legt den Wert der baseurl-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBaseurl(String value) {
		this.baseurl = value;
	}

	/**
	 * Ruft den Wert der file-Eigenschaft ab.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getFile() {
		return file;
	}

	/**
	 * Legt den Wert der file-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setFile(String value) {
		this.file = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String pDescription) {
		description = pDescription;
	}

	public void setGeneratorVersionCompatibleWithTemplate(boolean pGeneratorVersionCompatibleWithTemplate) {
		generatorVersionCompatibleWithTemplate = pGeneratorVersionCompatibleWithTemplate;
	}

	public boolean isGeneratorVersionCompatibleWithTemplate() {
		return generatorVersionCompatibleWithTemplate;
	}
}
