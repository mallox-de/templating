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
import javax.xml.bind.annotation.XmlType;


/**
 * Eclipse spezifische angaben, die in der
 * 				.project-Datei stehen.
 * 			
 * 
 * <p>Java-Klasse für projectDescription complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="projectDescription">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="projectDependencies" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="buildCommand" type="{http://www.mallox.de/schema/template}buildCommand" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="nature" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "projectDescription", propOrder = {
    "projectDependencies",
    "buildCommand",
    "nature"
})
public class ProjectDescription {

    protected List<String> projectDependencies;
    protected List<BuildCommand> buildCommand;
    protected List<String> nature;

    /**
     * Gets the value of the projectDependencies property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the projectDependencies property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProjectDependencies().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getProjectDependencies() {
        if (projectDependencies == null) {
            projectDependencies = new ArrayList<String>();
        }
        return this.projectDependencies;
    }

    /**
     * Gets the value of the buildCommand property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the buildCommand property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBuildCommand().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BuildCommand }
     * 
     * 
     */
    public List<BuildCommand> getBuildCommand() {
        if (buildCommand == null) {
            buildCommand = new ArrayList<BuildCommand>();
        }
        return this.buildCommand;
    }

    /**
     * Gets the value of the nature property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nature property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNature().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getNature() {
        if (nature == null) {
            nature = new ArrayList<String>();
        }
        return this.nature;
    }

}
