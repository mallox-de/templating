package de.mallox.eclipse.templates.velocity;

import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

import de.mallox.eclipse.templates.velocity.loader.HttpResourceLoader;
//import org.apache.velocity.runtime.resource.loader.URLResourceLoader;

public class RunVelocityTest2 {
	public static void main(String[] args) {

		
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "url");
		velocityEngine.setProperty("url." + RuntimeConstants.RESOURCE_LOADER + ".class", HttpResourceLoader.class.getName());
		
		velocityEngine.init();
		
		Template template = velocityEngine.getTemplate("projectsolution");

		
		VelocityContext context = new VelocityContext();
		context.put("name", "World");
		context.put("boolean", Boolean.valueOf("true"));
		context.put("int", new Integer(5));

		StringWriter writer = new StringWriter();
		template.merge(context, writer);

		System.out.println(writer.toString());
	}
}
