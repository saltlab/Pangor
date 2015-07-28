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
 * Executable class for calculating metrics of a list of repositories
 * 
 * Usage:
 * -i <file with 1 repository per line> -o <output CSV file with metrics>
 * 
 * Check GitMetricsExtratorOptions for default values
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
		List<String> uris = parseInputFile(options.getInputPath());

		/*
		 * Iterate over them and get statistics
		 */
		GitMetricsExtractorOutput metricsOutput = new GitMetricsExtractorOutput(options.getOutputPath());

		for (String uri : uris) {
			System.out.println("* Accessing repository: " + uri);
			GitProject project = GitProject.fromURI(uri, CHECKOUT_DIR);

			System.out.println("** Getting and writing metrics to output file");
			metricsOutput.output(project);
		}

		/* Close stream */
		metricsOutput.closeStream();

	}

	/**
	 * Takes a text file path with one repository URI per line and converts it
	 * to a List<String>
	 */
	private static List<String> parseInputFile(String filePath) {
		List<String> uris = new LinkedList<String>();

		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	uris.add(line);
		    }
		}

		catch(Exception e) {
			System.err.println("Error while reading URI file: " + e.getMessage());
		}

		return uris;
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
