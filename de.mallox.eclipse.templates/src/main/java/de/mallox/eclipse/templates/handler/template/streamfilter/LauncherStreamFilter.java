package de.mallox.eclipse.templates.handler.template.streamfilter;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;

import de.mallox.eclipse.templates.util.inputstream.TextLineFilterInputStream;

public class LauncherStreamFilter implements IStreamFilter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSuffix() {
		return "launch";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream manipulate(IFile pIFile, InputStream pSourceInputStream, String pEncoding) {
		TextLineFilterInputStream tQuoteWorkspaceVariables = new TextLineFilterInputStream(pSourceInputStream, pEncoding,
				"\\$\\{", "#[[\\${]]#");
		TextLineFilterInputStream tReplacedComponentName = new TextLineFilterInputStream(tQuoteWorkspaceVariables,
				pEncoding, "Template", "\\${ComponentName}");
		TextLineFilterInputStream tReplacedPackagePartPath = new TextLineFilterInputStream(tReplacedComponentName,
				pEncoding, "/template/", "/\\${PackagePartPath}/");
		TextLineFilterInputStream tReplacedPackagePart = new TextLineFilterInputStream(tReplacedPackagePartPath,
				pEncoding, "\\.template\\.", ".\\${PackagePart}.");
		return tReplacedPackagePart;
	}

}
