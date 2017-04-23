package de.mallox.eclipse.templates.model.template;

import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class ReadTestTemplate {

	public static void main(String[] args) throws Exception {
		Template tTemplate = new ReadTestTemplate().readTemplate();
		
		System.out.println(tTemplate.getName());
		
		List<Variable> variables = tTemplate.getVariable();
		for (Variable variable : variables) {
			System.out.println("var: " + variable.getName() + ": " + variable.getDescription());
		}
		
		List<ProjectTemplate> projectTemplates = tTemplate.getProjectTemplate();
		for (ProjectTemplate projectTemplate : projectTemplates) {
			System.out.println(projectTemplate.getName());
		}
	}
	
	public Template readTemplate() throws JAXBException, FileNotFoundException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Template.class);
		
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		Template tTemplate = (Template) unmarshaller.unmarshal(ReadTestTemplate.class.getResourceAsStream("test-template.xml"));

		return tTemplate;
	}
}
