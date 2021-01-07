package fr.univamu.ism.docometre.analyse.datamodel;

public class BatchDataProcessingItem {
	
	private String path;
	private boolean activated;
	
	public BatchDataProcessingItem(String path, boolean activated) {
		this.path = path;
		this.activated = activated;
	}
	
	@Override
	public String toString() {
		return path;
	}

	public String getPath() {
		return path;
	}

	public boolean isActivated() {
		return activated;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}
	
}