package de.mallox.eclipse.templates.handler.template.streamfilter;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link StreamFilterRegistry} is used to treat different file in different
 * ways. The files will be classified by its suffix.
 */
public class StreamFilterRegistry {

	/**
	 * {@link Map} which contains the different {@link IStreamFilter}.
	 */
	private final Map<String, IStreamFilter> registry = new HashMap<String, IStreamFilter>();

	public void add(final IStreamFilter pIFileHandler) {
		assert pIFileHandler != null : "pIFileHandler should not be null";
		registry.put(pIFileHandler.getSuffix(), pIFileHandler);
	}

	public IStreamFilter getFileHandler(final String pSuffix) {
		return registry.get(pSuffix);
	}
}
