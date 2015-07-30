package ca.ubc.ece.salt.sdjsb.git;

import org.kohsuke.args4j.Option;

public class GitMetricsExtractorOptions {

	@Option(name = "-h", aliases = { "--help" }, usage = "Display the help file.")
	private boolean help = false;

	@Option(name = "-i", aliases = { "--input" }, usage = "The file with git repositories. One repository per line.")
	private String inputPath = "./input/repositories.txt";

	@Option(name = "-o", aliases = { "--output" }, usage = "The output CSV file with the repository and the metrics.")
	private String outputPath = "./input/repositories_metrics.csv";

	public boolean getHelp() {
		return this.help;
	}

	public String getInputPath() {
		return this.inputPath;
	}

	public String getOutputPath() {
		return this.outputPath;
	}
}