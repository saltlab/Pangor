package ca.ubc.ece.salt.sdjsb.learning;

import org.kohsuke.args4j.Option;

public class LearningAnalysisOptions {

	@Option(name="-u", aliases={"--uri"}, usage="The uri of the public repository (e.g., https://github.com/qhanam/JSRepairClass.git).")
	private String host = null;

	@Option(name="-d", aliases={"--directory"}, usage="The git directory (e.g., /path/to/project/.git/).")
	private String directory = null;

	@Option(name="-h", aliases={"--help"}, usage="Display the help file.")
	private boolean help = false;

	@Option(name="-r", aliases={"--repositories"}, usage="The path to the file specifying the repositories to analyze.")
	private String repoFile = null;

	@Option(name="-ds", aliases={"--dataset"}, usage="The data set file to read.")
	private String dataSetPath = null;

	@Option(name="-s", aliases={"--supplement"}, usage="The folder path to place any supplementary files.")
	private String supplement = null;

	@Option(name = "-tr", aliases = { "--threads" }, usage = "The number of threads to be used.")
	private Integer nThreads = 6;

	public Integer getNThreads() {
		return this.nThreads;
	}

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

	public String getDataSetPath() {
		return this.dataSetPath;
	}

	public String getSupplementaryFolder() {
		return this.supplement;
	}

}