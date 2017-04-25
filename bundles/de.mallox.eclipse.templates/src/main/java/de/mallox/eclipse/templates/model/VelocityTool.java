package de.mallox.eclipse.templates.model;

public class VelocityTool {
	private String velocityName;
	
	private String className;
	
	private Object instance;
	
	private String encoding;

	public VelocityTool() {
		super();
	}

	public void setVelocityName(String pVelocityName) {
		velocityName = pVelocityName;
	}

	public void setClassName(String pClassName) {
		className = pClassName;
	}

	public String getVelocityName() {
		return velocityName;
	}
	
	public String getClassName() {
		return className;
	}
	
	public Object getInstance() {
		return instance;
	}

	public void setInstance(Object pInstance) {
		instance = pInstance;
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
