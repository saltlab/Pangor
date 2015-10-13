package ca.ubc.ece.salt.pangor.classify;

import org.kohsuke.args4j.Option;

public class ClassifyAnalysisOptions {

	@Option(name="-u", aliases={"--uri"}, usage="The uri of the public repository (e.g., https://github.com/Unitech/pm2).")
	private String host = null;

	@Option(name="-h", aliases={"--help"}, usage="Display the help file.")
	private boolean help = false;

	@Option(name="-r", aliases={"--repositories"}, usage="The path to the file specifying the list of repositories to analyze.")
	private String repoFile = null;

	@Option(name="-ds", aliases={"--dataset"}, usage="The data set file to write to.")
	private String dataSetPath = null;

	@Option(name="-s", aliases={"--supplement"}, usage="The folder path to place the supplementary files.")
	private String supplement = null;

	@Option(name = "-tr", aliases = { "--threads" }, usage = "The number of threads to be used.")
	private Integer nThreads = 6;

	@Option(name="-pp", aliases={"--preprocess"}, usage="Pre-process the AST before running GumTree. Expands ternary operators and short circuits.")
	private boolean preProcess = false;

	public Integer getNThreads() {
		return this.nThreads;
	}

	public String getURI() {
		return this.host;
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

	public boolean getPreProcess() {
		return preProcess;
	}

}
