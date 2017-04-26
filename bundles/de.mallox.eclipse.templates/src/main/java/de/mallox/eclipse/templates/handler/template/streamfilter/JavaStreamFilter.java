package de.mallox.eclipse.templates.handler.template.streamfilter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;

import de.mallox.eclipse.templates.util.inputstream.TextLineFilterInputStream;

public class JavaStreamFilter implements IStreamFilter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSuffix() {
		return "java";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream manipulate(IFile pIFile, InputStream pSourceInputStream, String pEncoding) {
		final ICompilationUnit tCompilationUnit = JavaCore.createCompilationUnitFrom(pIFile);
		if (tCompilationUnit != null) {
			TextLineFilterInputStream tReplacedPackageName = new TextLineFilterInputStream(pSourceInputStream, pEncoding,
					"\\.template\\.", ".\\${PackagePart}.");
			TextLineFilterInputStream tReplacedComponentName = new TextLineFilterInputStream(tReplacedPackageName,
					pEncoding, "Template", "\\${ComponentName}");
			return new SequenceInputStream(getHeaderInuputStream(pEncoding), tReplacedComponentName);
		}
		return pSourceInputStream;
	}

	private static InputStream getHeaderInuputStream(String pEncoding) {
		final StringBuilder tBuilder = new StringBuilder();

		tBuilder.append("/*\n");
		tBuilder.append(
				" * Based on $Template.getName() $Template.getVersion() created at ${date} with GeneratorPluginVersion ${GeneratorPluginVersion}.\n");
		tBuilder.append(" */\n");

		try {
			return new ByteArrayInputStream(tBuilder.toString().getBytes(pEncoding));
		} catch (final UnsupportedEncodingException e) {
			// NOP
		}
		return new ByteArrayInputStream(new byte[0]);
	}

}
