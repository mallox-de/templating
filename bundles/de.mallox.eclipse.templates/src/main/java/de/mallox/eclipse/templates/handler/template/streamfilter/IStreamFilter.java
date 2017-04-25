package de.mallox.eclipse.templates.handler.template.streamfilter;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;

/**
 * {@link IStreamFilter} is used to manipulate the given {@link InputStream} to
 * change or add content to the file.
 */
public interface IStreamFilter {

	/**
	 * Suffix under this {@link IStreamFilter} will be registered.
	 * 
	 * @return Suffix of this {@link IStreamFilter}.
	 */
	String getSuffix();

	/**
	 * Change or add content to the given {@link InputStream}.
	 * 
	 * @param pIFile
	 *            {@link IFile}, which can be used to determine the type of the
	 *            file in more detail.
	 * @param pSourceInputStream
	 *            {@link InputStream}
	 * @param pEncoding of the source and destination {@link InputStream}.
	 * @return {@link InputStream}.
	 */
	InputStream manipulate(IFile pIFile, InputStream pSourceInputStream, String pEncoding);
}
