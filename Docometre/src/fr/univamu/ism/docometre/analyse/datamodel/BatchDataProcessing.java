package fr.univamu.ism.docometre.analyse.datamodel;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;

import fr.univamu.ism.docometre.dacqsystems.AbstractElement;

public class BatchDataProcessing extends AbstractElement {
	
	public static final long serialVersionUID =  1L;
	
	private ArrayList<BatchDataProcessingItem> processes = new ArrayList<>();
	private ArrayList<BatchDataProcessingItem> subjects = new ArrayList<>();

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initializeObservers() {
		// TODO Auto-generated method stub

	}
	
	private String getPath(IResource resource) {
		return resource.getFullPath().toPortableString();
	}
	
	//Processes
	public void addProcess(IResource process) {
		BatchDataProcessingItem batchDataProcessingItem = new BatchDataProcessingItem(getPath(process), true);
		processes.add(batchDataProcessingItem);
	}
	
	public void removeProcess(int index) {
		processes.remove(index);
	}
	
	public void moveProcessUp(int index) {
		if(index > 0) {
			BatchDataProcessingItem batchDataProcessingItem = processes.remove(index);
			index--;
			processes.add(index, batchDataProcessingItem);
		}
	}
	
	public void moveProcessDown(int index) {
		if(index < processes.size() - 1) {
			BatchDataProcessingItem batchDataProcessingItem = processes.remove(index);
			index++;
			processes.add(index, batchDataProcessingItem);
		}
	}
	
	public BatchDataProcessingItem[] getProcesses() {
		BatchDataProcessingItem[] items = processes.toArray(new BatchDataProcessingItem[processes.size()]);
		for (BatchDataProcessingItem item : items) {
			IResource process = ResourcesPlugin.getWorkspace().getRoot().findMember(item.getPath());
			if(process == null) processes.remove(item);
		}
		return processes.toArray(new BatchDataProcessingItem[processes.size()]);
	}
	
	// Subjects
	public void addSubject(IResource subject) {
		BatchDataProcessingItem batchDataProcessingItem = new BatchDataProcessingItem(getPath(subject), true);
		subjects.add(batchDataProcessingItem);
	}
	
	public void removeSubject(int index) {
		subjects.remove(index);
	}
	
	public void moveSubjectUp(int index) {
		if(index > 0) {
			BatchDataProcessingItem batchDataProcessingItem = subjects.remove(index);
			index--;
			subjects.add(index, batchDataProcessingItem);
		}
	}
	
	public void moveSubjectDown(int index) {
		if(index < subjects.size() - 1) {
			BatchDataProcessingItem batchDataProcessingItem = subjects.remove(index);
			index++;
			subjects.add(index, batchDataProcessingItem);
		}
	}
	
	public BatchDataProcessingItem[] getSubjects() {
		BatchDataProcessingItem[] items = subjects.toArray(new BatchDataProcessingItem[subjects.size()]);
		for (BatchDataProcessingItem item : items) {
			IResource subject = ResourcesPlugin.getWorkspace().getRoot().findMember(item.getPath());
			if(subject == null) subjects.remove(item);
		}
		return subjects.toArray(new BatchDataProcessingItem[subjects.size()]);
	}
}