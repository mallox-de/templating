/**
 * (c) Copyright Mirasol Op'nWorks Inc. 2002, 2003. 
 * http://www.opnworks.com
 * Created on Apr 2, 2003 by lgauthier@opnworks.com
 * 
 */

package de.mallox.eclipse.templates.preferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TemplatePreferenceDataList {

	private List<TemplatePreferenceData> tasks = new ArrayList<>();
	private Set<ITemplatePreferenceDataListViewer> changeListeners = new HashSet<>();

	/**
	 * Constructor
	 */
	public TemplatePreferenceDataList() {
		super();
	}
	

	/**
	 * Return the collection of tasks
	 */
	public List<TemplatePreferenceData> getTemplatePreferenceDataList() {
		return tasks;
	}
	
	/**
	 * Add a new task to the collection of tasks
	 * @param prefData 
	 */
	public void addTask() {
		addTask(new TemplatePreferenceData("<name>", "<baseurl>", "templates.xml"));
	}
	
	/**
	 * Add a new task to the collection of tasks
	 * @param pPrefData 
	 */
	public void addTask(TemplatePreferenceData pPrefData) {
		tasks.add(tasks.size(), pPrefData);
		Iterator<ITemplatePreferenceDataListViewer> tChangeListenerIterator = changeListeners.iterator();
		while (tChangeListenerIterator.hasNext())
			((ITemplatePreferenceDataListViewer) tChangeListenerIterator.next()).addTask(pPrefData);
	}

	/**
	 * @param pPrefData
	 */
	public void removeTask(TemplatePreferenceData pPrefData) {
		tasks.remove(pPrefData);
		Iterator<ITemplatePreferenceDataListViewer> tChangeListenerIterator = changeListeners.iterator();
		while (tChangeListenerIterator.hasNext())
			((ITemplatePreferenceDataListViewer) tChangeListenerIterator.next()).removeTask(pPrefData);
	}

	/**
	 * @param pPrefData
	 */
	public void taskChanged(TemplatePreferenceData pPrefData) {
		Iterator<ITemplatePreferenceDataListViewer> tChangeListenerIterator = changeListeners.iterator();
		while (tChangeListenerIterator.hasNext())
			((ITemplatePreferenceDataListViewer) tChangeListenerIterator.next()).updateTask(pPrefData);
	}

	/**
	 * @param pViewer
	 */
	public void removeChangeListener(ITemplatePreferenceDataListViewer pViewer) {
		changeListeners.remove(pViewer);
	}

	/**
	 * @param pViewer
	 */
	public void addChangeListener(ITemplatePreferenceDataListViewer pViewer) {
		changeListeners.add(pViewer);
	}

}
