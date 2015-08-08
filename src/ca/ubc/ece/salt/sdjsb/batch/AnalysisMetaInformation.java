package ca.ubc.ece.salt.sdjsb.batch;

public class AnalysisMetaInformation {

	/** The number of commits inspected. **/
	public int totalCommits;

	/** The number of bug fixing commits analyzed. **/
	public int bugFixingCommits;

	/** The identifier for the project. **/
	public String projectID;

	/** The homepage for the project. **/
	public String projectHomepage;

	/** The path to the source file where the bug is present. **/
	public String buggyFile;

	/** The path to the source file where the bug is repaired. **/
	public String repairedFile;

	/** The ID for the commit where the bug is present. **/
	public String buggyCommitID;

	/** The ID for the commit where the bug is repaired. **/
	public String repairedCommitID;

	/** The buggy source code. **/
	public String buggyCode;

	/** The repaired source code. **/
	public String repairedCode;

	/**
	 * @param totalCommits
	 * @param bugFixingCommits
	 * @param projectID
	 * @param buggyFile
	 * @param repairedFile
	 * @param buggyCommitID
	 * @param repairedCommitID
	 * @param buggyCode
	 * @param repairedCode
	 */
	public AnalysisMetaInformation(int totalCommits, int bugFixingCommits,
			String projectID,
			String projectHomepage,
			String buggyFile, String repairedFile,
			String buggyCommitID, String repairedCommitID,
			String buggyCode, String repairedCode) {

		this.totalCommits = totalCommits;
		this.bugFixingCommits = bugFixingCommits;
		this.projectID = projectID;
		this.projectHomepage = projectHomepage;
		this.buggyFile = buggyFile;
		this.repairedFile = repairedFile;
		this.buggyCommitID = buggyCommitID;
		this.repairedCommitID = repairedCommitID;
		this.buggyCode = buggyCode;
		this.repairedCode = repairedCode;

	}

}