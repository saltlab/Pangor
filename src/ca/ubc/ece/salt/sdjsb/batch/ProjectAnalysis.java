package ca.ubc.ece.salt.sdjsb.batch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class ProjectAnalysis {
	
	public static final String CHECKOUT_DIR =  new String("repositories");

	/**
	 * Inspects bug fixing commits to classify repairs.
	 * @param args Usage: ProjectAnalysis /path/to/project/.git
	 * @throws IOException
	 * @throws GitAPIException
	 */
	public static void main(String[] args) throws Exception {

		ProjectAnalysisOptions options = new ProjectAnalysisOptions();
		CmdLineParser parser = new CmdLineParser(options);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			ProjectAnalysis.printUsage(e.getMessage(), parser);
			return;
		}
		
		/* Print the help page. */
		if(options.getHelp()) {
			ProjectAnalysis.printHelp(parser);
			return;
		}
		
        GitProjectAnalysis gitProjectAnalysis;

		/* A project folder was given. */
		if(options.getGitDirectory() != null) {

			try {
                gitProjectAnalysis = GitProjectAnalysis.fromDirectory(options.getGitDirectory(), "Unknown");
			} 
			catch(GitProjectAnalysisException e) {
                ProjectAnalysis.printUsage(e.getMessage(), parser);
                return;
			}

			ProjectAnalysis.analyzeAndPrint(gitProjectAnalysis, options);

		}
		/* A URI was given. */
		else if(options.getURI() != null) {

			try {
                gitProjectAnalysis = GitProjectAnalysis.fromURI(options.getURI(), ProjectAnalysis.CHECKOUT_DIR);
			} 
			catch(GitProjectAnalysisException e) {
                ProjectAnalysis.printUsage(e.getMessage(), parser);
                return;
			}
			
			ProjectAnalysis.analyzeAndPrint(gitProjectAnalysis, options);

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
					gitProjectAnalysis = GitProjectAnalysis.fromURI(uri, ProjectAnalysis.CHECKOUT_DIR);
				} 
				catch(GitProjectAnalysisException e) {
					ProjectAnalysis.printUsage(e.getMessage(), parser);
					return;
				}
				
				ProjectAnalysis.analyzeAndPrint(gitProjectAnalysis, options);
				
				options.setAppend(true);
				
			}
		
			
		}
		else {
			System.out.println("No repository given.");
			ProjectAnalysis.printUsage("No repository given.", parser);
			return;
		}

	}
	
	/**
	 * Performs the analysis on the project and prints according to the 
	 * options that are selected.
	 * @param analysis The analysis to execute.
	 * @param options The command line options.
	 * @throws Exception
	 */
	private static void analyzeAndPrint(GitProjectAnalysis analysis, ProjectAnalysisOptions options) throws Exception {

		/* Perform the analysis (may take some time). */
		analysis.analyze();
		
		/* Print what we need. */
		AlertPrinter printer = new AlertPrinter(analysis.getAnalysisResult());
		if(!options.printCustom()) printer.printSummary(options.printAlerts());
		if(options.printLatex()) printer.printLatexTable();
		if(options.printCustom()) printer.printCustom(options.getOutfile(), options.getAppend());
		
	}

	/**
	 * Prints the help file for main.
	 * @param parser The args4j parser.
	 */
	private static void printHelp(CmdLineParser parser) {
        System.out.print("Usage: ProjectAnalysis ");
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
        System.out.print("Usage: ProjectAnalysis ");
        parser.setUsageWidth(Integer.MAX_VALUE);
        parser.printSingleLineUsage(System.out);
        System.out.println("");
        return;
	}
	
}