package ca.ubc.ece.salt.sdjsb.learning;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ca.ubc.ece.salt.sdjsb.batch.GitProjectAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.GitProjectAnalysisException;

public class LearningMain {

	/** The directory where repositories are checked out. **/
	public static final String CHECKOUT_DIR =  new String("repositories");

	/**
	 * Creates the learning data set for extracting repair patterns.
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		LearningAnalysisRunner runner = new LearningAnalysisRunner(Arrays.asList("fs"));
		LearningOptions options = new LearningOptions();
		CmdLineParser parser = new CmdLineParser(options);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			LearningMain.printUsage(e.getMessage(), parser);
			return;
		}
		
		/* Print the help page. */
		if(options.getHelp()) {
			LearningMain.printHelp(parser);
			return;
		}
		
        GitProjectAnalysis gitProjectAnalysis;

		/* A URI was given. */
		if(options.getURI() != null) {

			try {
                gitProjectAnalysis = GitProjectAnalysis.fromURI(options.getURI(), CHECKOUT_DIR, runner);
			} 
			catch(GitProjectAnalysisException e) {
                LearningMain.printUsage(e.getMessage(), parser);
                return;
			}
			
			gitProjectAnalysis.analyze();

		}
		/* A list of URIs was given. */
		else if(options.getRepoFile() != null) {
			
			/* Parse the file into a list of URIs. */
			List<String> uris = new LinkedList<String>();
			
			try(BufferedReader br = new BufferedReader(new FileReader(options.getRepoFile()))) {
			    for(String line; (line = br.readLine()) != null; ) {
			    	uris.add(line);
			    }
			}
			catch(Exception e) {
				System.err.println("Error while reading URI file: " + e.getMessage());
			}
			
			/* Analyze all projects. */
			for(String uri : uris) {

				try {
					gitProjectAnalysis = GitProjectAnalysis.fromURI(uri, LearningMain.CHECKOUT_DIR, runner);
				} 
				catch(GitProjectAnalysisException e) {
					LearningMain.printUsage(e.getMessage(), parser);
					return;
				}
				
				/* Perform the analysis (this may take some time) */
				gitProjectAnalysis.analyze();
				
			}
			
		}
		else {
			System.out.println("No repository given.");
			LearningMain.printUsage("No repository given.", parser);
			return;
		}
		
		/* Print the data set. */
		runner.printResults(options.getOutfile(), options.getSupplementaryFolder());

	}

	/**
	 * Prints the help file for main.
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
