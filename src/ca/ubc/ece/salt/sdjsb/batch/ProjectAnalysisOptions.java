package ca.ubc.ece.salt.sdjsb.batch;

import org.kohsuke.args4j.Option;

public class ProjectAnalysisOptions {

	@Option(name="-u", aliases={"--uri"}, usage="The uri of the public repository (e.g., https://github.com/qhanam/JSRepairClass.git).")
	private String host = null;
	
	@Option(name="-d", aliases={"--directory"}, usage="The git directory (e.g., /path/to/project/.git/).")
	private String directory = null;
	
	@Option(name="-h", aliases={"--help"}, usage="Display the help file.")
	private boolean help = false;
	
	@Option(name="-t", aliases={"--latex"}, usage="Output summary to latex table.")
	private boolean latex = false;
	
	@Option(name="-a", aliases={"--alerts"}, usage="Print individual alert descriptions.")
	private boolean alerts = false;
	
	@Option(name="-c", aliases={"--custom"}, usage="Prints custom alerts.")
	private boolean custom = false;
	
	@Option(name="-r", aliases={"--repositories"}, usage="The path to the file specifying the repositories to analyze.")
	private String repoFile = null;
	
	@Option(name="-o", aliases={"--outfile"}, usage="The file path to output to.")
	private String outFile = null;
	
	@Option(name="-s", aliases={"--supplement"}, usage="The folder path to place any supplementary files.")
	private String supplement = null;
	
	@Option(name="-p", aliases={"--append"}, usage="Appends output to the output file.")
	private boolean append = false;

	public String getURI() {
		return this.host;
	}

	public String getGitDirectory() {
		return this.directory;
	}
	
	public boolean getHelp() {
		return this.help;
	}
	
	public boolean printLatex() {
		return this.latex;
	}
	
	public boolean printAlerts() {
		return this.alerts;
	}
	
	public boolean printCustom() {
		return this.custom;
	}
	
	public String getRepoFile() {
		return this.repoFile;
	}
	
	public String getOutfile() {
		return this.outFile;
	}
	
	public String getSupplementaryFolder() {
		return this.supplement;
	}
	
	public boolean getAppend() {
		return this.append;
	}
	
	public void setAppend(boolean append) {
		this.append = append;
	}

}
