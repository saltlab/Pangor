package ca.ubc.ece.salt.sdjsb.batch;

import ca.ubc.ece.salt.sdjsb.alert.Alert;

public class BatchAlert extends Alert {
	
	private Alert alert;

    public String bugFixingCommit;
    public String buggyCommit;
    public String oldFile;
    public String newFile;
	
	public BatchAlert(Alert alert, String buggyCommit, String bugFixingCommit, String oldFile, String newFile) {
		super(alert.getType(), alert.getSubType());
		this.alert = alert;
		this.bugFixingCommit = bugFixingCommit;
		this.buggyCommit = buggyCommit;
		this.oldFile = oldFile;
		this.newFile = newFile;
	}
	
	@Override
	public String getID() {
		return alert.getID();
	}
	
	@Override
	public String getSourceCode() {
		return alert.getSourceCode();
	}
	
	@Override
	public String getDestinationCode() {
		return alert.getDestinationCode();
	}
	
	@Override
	public String getFeatureVector(String project, String sourceFile, String destinationFile, String buggyCommit, String repairedCommit) {
		return alert.getFeatureVector(project, this.oldFile, this.newFile, this.buggyCommit, this.bugFixingCommit);
	}

	@Override
	public String getIdentifier() {
		return alert.getType() + "_" + alert.getSubType();
	}

	@Override
	public String getShortDescription() {
		return alert.getShortDescription();
	}
	
	@Override
	public String getLongDescription() {
		return this.buggyCommit + ":" + this.bugFixingCommit + ":" + this.newFile + ":" + alert.getLongDescription();
	}

	@Override
	protected String getAlertDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getAlertExplanation() {
		// TODO Auto-generated method stub
		return null;
	}

}
