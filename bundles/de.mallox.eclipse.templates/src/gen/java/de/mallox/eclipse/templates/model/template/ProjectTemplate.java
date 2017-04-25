//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.04.21 um 09:20:25 PM CEST 
//


package de.mallox.eclipse.templates.model.template;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Beschreibt ein Projekt, welches im Rahmen des
 * 				Templates angelegt werden soll.
 * 			
 * 
 * <p>Java-Klasse für projectTemplate complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="projectTemplate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="projectDescription" type="{http://www.mallox.de/schema/template}projectDescription" minOccurs="0"/>
 *         &lt;element name="classpathentries" type="{http://www.mallox.de/schema/template}classpathentry" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="files" type="{http://www.mallox.de/schema/template}file" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="launcher" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="encoding" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="condition" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="conditionValue" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="alreadyExists" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="compileWorkspace" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "projectTemplate", propOrder = {
    "description",
    "projectDescription",
    "classpathentries",
    "files",
    "launcher"
})
public class ProjectTemplate {

    @XmlElement(required = true)
    protected String description;
    protected ProjectDescription projectDescription;
    protected List<Classpathentry> classpathentries;
    protected List<File> files;
    protected List<String> launcher;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "encoding")
    protected String encoding;
    @XmlAttribute(name = "condition")
    protected String condition;
    @XmlAttribute(name = "conditionValue")
    protected String conditionValue;
    @XmlAttribute(name = "alreadyExists")
    protected Boolean alreadyExists;
    @XmlAttribute(name = "compileWorkspace")
    protected Boolean compileWorkspace;

    /**
     * Ruft den Wert der description-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Legt den Wert der description-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Ruft den Wert der projectDescription-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ProjectDescription }
     *     
     */
    public ProjectDescription getProjectDescription() {
        return projectDescription;
    }

    /**
     * Legt den Wert der projectDescription-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ProjectDescription }
     *     
     */
    public void setProjectDescription(ProjectDescription value) {
        this.projectDescription = value;
    }

    /**
     * Gets the value of the classpathentries property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the classpathentries property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getClasspathentries().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Classpathentry }
     * 
     * 
     */
    public List<Classpathentry> getClasspathentries() {
        if (classpathentries == null) {
            classpathentries = new ArrayList<Classpathentry>();
        }
        return this.classpathentries;
    }

    /**
     * Gets the value of the files property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the files property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFiles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link File }
     * 
     * 
     */
    public List<File> getFiles() {
        if (files == null) {
            files = new ArrayList<File>();
        }
        return this.files;
    }

    /**
     * Gets the value of the launcher property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the launcher property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLauncher().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
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
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der encoding-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
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
     *     {@link String }
     *     
     */
    public void setEncoding(String value) {
        this.encoding = value;
    }

    /**
     * Ruft den Wert der condition-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Legt den Wert der condition-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCondition(String value) {
        this.condition = value;
    }

    /**
     * Ruft den Wert der conditionValue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConditionValue() {
        return conditionValue;
    }

    /**
     * Legt den Wert der conditionValue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConditionValue(String value) {
        this.conditionValue = value;
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

}
