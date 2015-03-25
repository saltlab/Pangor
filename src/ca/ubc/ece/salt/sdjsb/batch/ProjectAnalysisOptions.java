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

}
