package ca.ubc.ece.salt.sdjsb.classify;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ca.ubc.ece.salt.sdjsb.batch.GitProjectAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.GitProjectAnalysisException;
import ca.ubc.ece.salt.sdjsb.batch.GitProjectAnalysisTask;
import ca.ubc.ece.salt.sdjsb.learning.LearningAnalysisMain;

public class ClassifyAnalysisMain {

	protected static final Logger logger = LogManager.getLogger(LearningAnalysisMain.class);

	/** The directory where repositories are checked out. **/
	public static final String CHECKOUT_DIR =  new String("repositories");

	/**
	 * Inspects bug fixing commits to classify repairs.
	 * @param args Usage: ProjectAnalysis /path/to/project/.git
	 * @throws IOException
	 * @throws GitAPIException
	 */
	public static void main(String[] args) throws Exception {

		ClassifyAnalysisOptions options = new ClassifyAnalysisOptions();
		CmdLineParser parser = new CmdLineParser(options);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			ClassifyAnalysisMain.printUsage(e.getMessage(), parser);
			return;
		}

		/* Print the help page. */
		if(options.getHelp()) {
			ClassifyAnalysisMain.printHelp(parser);
			return;
		}

		/* Create the runner that will run the analysis. */
		ClassifyAnalysisRunner runner = new ClassifyAnalysisRunner(options.getDataSetPath(), options.getSupplementaryFolder(), options.getPreProcess());

        GitProjectAnalysis gitProjectAnalysis;

		/* A URI was given. */
		if(options.getURI() != null) {

			try {
                gitProjectAnalysis = GitProjectAnalysis.fromURI(options.getURI(), ClassifyAnalysisMain.CHECKOUT_DIR, runner);
                gitProjectAnalysis.analyze();
			}
			catch(GitProjectAnalysisException e) {
				e.printStackTrace(System.err);
                return;
			}

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

			/*
			 * Create a pool of threads and use a CountDownLatch to check when
			 * all threads are done.
			 * http://stackoverflow.com/questions/1250643/how-to-wait-for-all-
			 * threads-to-finish-using-executorservice
			 *
			 * I was going to create a list of Callable objects and use
			 * executor.invokeAll, but this would remove the start of the
			 * execution of the tasks from the loop to outside the loop, which
			 * would mean all git project initializations would have to happen
			 * before starting the analysis.
			 */
			ExecutorService executor = Executors.newFixedThreadPool(options.getNThreads());
			CountDownLatch latch = new CountDownLatch(uris.size());

			/* Analyze all projects. */
			for(String uri : uris) {

				try {
					/* Build git repository object. */
					gitProjectAnalysis = GitProjectAnalysis.fromURI(uri, ClassifyAnalysisMain.CHECKOUT_DIR, runner);

					/* Perform the analysis (this may take some time) */
					executor.submit(new GitProjectAnalysisTask(gitProjectAnalysis, latch));
				}
				catch(Exception e) {
					e.printStackTrace(System.err);
					logger.error("[IMPORTANT] Project {} threw an exception", uri);
					logger.error(e);
					continue;
				}
			}

			/* Wait for all threads to finish their work */
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}

		}
		else {
			System.out.println("No repository given.");
			ClassifyAnalysisMain.printUsage("No repository given.", parser);
			return;
		}

	}

	/**
	 * Prints the help file for main.
	 * @param parser The args4j parser.
	 */
	private static void printHelp(CmdLineParser parser) {
        System.out.print("Usage: ClassifyMain ");
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