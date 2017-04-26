//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2017.04.21 um 09:20:25 PM CEST 
//


package de.mallox.eclipse.templates.model.template;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the de.mallox.eclipse.templates.model.template package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: de.mallox.eclipse.templates.model.template
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Template }
     * 
     */
    public Template createTemplate() {
        return new Template();
    }

    /**
     * Create an instance of {@link Variable }
     * 
     */
    public Variable createVariable() {
        return new Variable();
    }

    /**
     * Create an instance of {@link VariableCombination }
     * 
     */
    public VariableCombination createVariableCombination() {
        return new VariableCombination();
    }

    /**
     * Create an instance of {@link ProjectTemplate }
     * 
     */
    public ProjectTemplate createProjectTemplate() {
        return new ProjectTemplate();
    }

    /**
     * Create an instance of {@link VelocityTool }
     * 
     */
    public VelocityTool createVelocityTool() {
        return new VelocityTool();
    }

    /**
     * Create an instance of {@link Classpathentry }
     * 
     */
    public Classpathentry createClasspathentry() {
        return new Classpathentry();
    }

    /**
     * Create an instance of {@link File }
     * 
     */
    public File createFile() {
        return new File();
    }

    /**
     * Create an instance of {@link BuildCommand }
     * 
     */
    public BuildCommand createBuildCommand() {
        return new BuildCommand();
    }

    /**
     * Create an instance of {@link ProjectDescription }
     * 
     */
    public ProjectDescription createProjectDescription() {
        return new ProjectDescription();
    }

    /**
     * Create an instance of {@link Attribute }
     * 
     */
    public Attribute createAttribute() {
        return new Attribute();
    }

}
