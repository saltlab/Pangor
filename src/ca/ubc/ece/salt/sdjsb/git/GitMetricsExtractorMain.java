package ca.ubc.ece.salt.sdjsb.git;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ca.ubc.ece.salt.sdjsb.batch.GitProjectAnalysisException;

/**
 * Executable class for calculating metrics of a list of repositories Input
 * Usage: -i <file with 1 repository per line> -o <output CSV file with metrics>
 * Check GitMetricsExtratorOptions for default values
 * 
 * Input can either be: 
 *  (1) file with one repository uri per line (for applications) or
 *  (2) file with repository,number_of_downloads (for modules)
 */
public class GitMetricsExtractorMain {
	/** The directory where repositories are checked out. **/
	public static final String CHECKOUT_DIR = new String("repositories");

	public static void main(String[] args)
			throws InvalidRemoteException, TransportException, GitProjectAnalysisException, GitAPIException {
		GitMetricsExtractorOptions options = new GitMetricsExtractorOptions();
		CmdLineParser parser = new CmdLineParser(options);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			GitMetricsExtractorMain.printUsage(e.getMessage(), parser);
			return;
		}

		/* Print the help page. */
		if (options.getHelp()) {
			GitMetricsExtractorMain.printHelp(parser);
			return;
		}

		/* Get the input list */
		List<String> lines = parseInputFile(options.getInputPath());

		/*
		 * Iterate over them and get statistics
		 */
		GitMetricsExtractorOutput metricsOutput = new GitMetricsExtractorOutput(options.getOutputPath());

		for (String line : lines) {
			GitProject project;

			/*
			 * If line has a ",", this is a module csv file with the number of
			 * downloads over the last month
			 */
			if (line.contains(",")) {
				String uri = line.split(",")[0];
				Integer downloadsLastMonth = Integer.parseInt(line.split(",")[1]);

				project = GitProject.fromURI(uri, CHECKOUT_DIR);
				project.setDownloadsLastMonth(downloadsLastMonth);
			} else {
				project = GitProject.fromURI(line, CHECKOUT_DIR);
			}

			System.out.println("* Accessing repository: " + project.getURI());

			/* Get and write metrics to output file */
			metricsOutput.output(project);
		}

		/* Close stream */
		metricsOutput.closeStream();

	}

	/**
	 * Takes a text file path and return its lines on a List<String>
	 */
	private static List<String> parseInputFile(String filePath) {
		List<String> lines = new LinkedList<String>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
		    for(String line; (line = br.readLine()) != null; ) {
				// Ignore comments (starting with #) and blank lines
				if (line.isEmpty() || line.charAt(0) == '#')
		    		continue;

				lines.add(line);
		    }
		}

		catch(Exception e) {
			System.err.println("Error while reading URI file: " + e.getMessage());
		}

		return lines;
	}

	/**
	 * Prints the help file for main.
	 *
	 * @param parser The args4j parser.
	 */
	private static void printHelp(CmdLineParser parser) {
		System.out.print("Usage: DataSetMain ");
		parser.setUsageWidth(Integer.MAX_VALUE);
		parser.printSingleLineUsage(System.out);
		System.out.println("\n");
		parser.printUsage(System.out);
		System.out.println("");
		return;
	}

	/**
	 * Prints the usage of main.
	 *
	 * @param error The error message that triggered the usage message.
	 * @param parser The args4j parser.
	 */
	private static void printUsage(String error, CmdLineParser parser) {
		System.out.println(error);
		System.out.print("Usage: DataSetMain ");
		parser.setUsageWidth(Integer.MAX_VALUE);
		parser.printSingleLineUsage(System.out);
		System.out.println("");
		return;
	}

}
