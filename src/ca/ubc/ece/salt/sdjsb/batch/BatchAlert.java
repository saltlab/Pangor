package ca.ubc.ece.salt.sdjsb.batch;

import ca.ubc.ece.salt.sdjsb.checker.Alert;

public class BatchAlert extends Alert {
	
	private Alert alert;

    public String bugFixingCommit;
    public String buggyCommit;
    public String oldFile;
    public String newFile;
	
	public BatchAlert(Alert alert, String bugFixingCommit, String buggyCommit, String oldFile, String newFile) {
		super(alert.getType(), alert.getSubType());
		this.alert = alert;
		this.bugFixingCommit = bugFixingCommit;
		this.buggyCommit = buggyCommit;
		this.oldFile = oldFile;
		this.newFile = newFile;
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
