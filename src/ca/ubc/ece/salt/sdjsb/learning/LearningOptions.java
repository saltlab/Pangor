package ca.ubc.ece.salt.sdjsb.learning;

import org.kohsuke.args4j.Option;

public class LearningOptions {

	@Option(name="-u", aliases={"--uri"}, usage="The uri of the public repository (e.g., https://github.com/qhanam/JSRepairClass.git).")
	private String host = null;
	
	@Option(name="-d", aliases={"--directory"}, usage="The git directory (e.g., /path/to/project/.git/).")
	private String directory = null;
	
	@Option(name="-h", aliases={"--help"}, usage="Display the help file.")
	private boolean help = false;
	
	@Option(name="-r", aliases={"--repositories"}, usage="The path to the file specifying the repositories to analyze.")
	private String repoFile = null;
	
	@Option(name="-o", aliases={"--outfile"}, usage="The file path to output to.")
	private String outFile = null;
	
	@Option(name="-s", aliases={"--supplement"}, usage="The folder path to place any supplementary files.")
	private String supplement = null;
	
	public String getURI() {
		return this.host;
	}

	public String getGitDirectory() {
		return this.directory;
	}
	
	public boolean getHelp() {
		return this.help;
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

}