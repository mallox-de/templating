//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Ã„nderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2016.02.22 um 12:18:20 AM CET 
//

package de.mallox.eclipse.templates.model;

/**
 * {@link VariableEntry} beschreibt eine Variable, die über den Wizard abgefragt
 * werden kann, die später als Velocity-Variable verfügbar ist.
 */
public class VariableEntry {

	private String description;
	private String name;
	private String type;
	private String _default;
	private boolean optional;

	private String value;

	public String getValue() {
		if (value == null) {
			value = getDefault();
		}
		return value;
	}

	public void setValue(String pValue) {
		value = pValue;
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
	 * Ruft den Wert der type-Eigenschaft ab.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getType() {
		return type;
	}

	/**
	 * Legt den Wert der type-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setType(String value) {
		this.type = value;
	}

	/**
	 * Ruft den Wert der default-Eigenschaft ab.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDefault() {
		return _default;
	}

	/**
	 * Legt den Wert der default-Eigenschaft fest.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDefault(String value) {
		this._default = value;
	}

	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean pOptional) {
		optional = pOptional;
	}
}
