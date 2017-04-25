package de.mallox.eclipse.templates.velocity;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

public class RunVelocityTest {
	public static void main(String[] args) {

		
		VelocityEngine velocityEngine = new VelocityEngine();
		String tPath = RunVelocityTest.class.getResource(".").getFile();
		velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, tPath);
		
		velocityEngine.init();

		Template template = velocityEngine.getTemplate("helloworld.vm");

		VelocityContext context = new VelocityContext();
		context.put("name", "World");
		context.put("boolean", Boolean.valueOf("false"));
		context.put("int", new Integer(5));

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		System.out.println(writer.toString());
	}
}
