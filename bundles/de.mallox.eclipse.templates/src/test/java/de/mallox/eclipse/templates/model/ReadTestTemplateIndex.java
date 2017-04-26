package de.mallox.eclipse.templates.model;

import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class ReadTestTemplateIndex {

	public static void main(String[] args) throws Exception {
		TemplateIndex tTemplateIndex = new ReadTestTemplateIndex().readTemplateIndex();

		System.out.println(tTemplateIndex.getName());

		List<TemplateRef> templates = tTemplateIndex.getTemplateRef();
		for (TemplateRef template : templates) {

			System.out.println(template.getName() + " (" + template.getBaseurl() + ":" + template.getFile());
		}
	}

	public TemplateIndex readTemplateIndex() throws JAXBException, FileNotFoundException {
		JAXBContext jaxbContext = JAXBContext.newInstance(TemplateIndex.class);

		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		TemplateIndex tTemplateIndex = (TemplateIndex) unmarshaller
				.unmarshal(ReadTestTemplateIndex.class.getResourceAsStream("test-template-index.xml"));

		return tTemplateIndex;
	}
}
