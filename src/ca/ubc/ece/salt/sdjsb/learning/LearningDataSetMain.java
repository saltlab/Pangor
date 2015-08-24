package ca.ubc.ece.salt.sdjsb.learning;

import java.util.Arrays;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.KeywordFilter;
import ca.ubc.ece.salt.sdjsb.analysis.learning.KeywordFilter.FilterType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.LearningDataSet;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordDefinition.KeywordType;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordUse.KeywordContext;

public class LearningDataSetMain {

	/**
	 * Creates the learning data set for extracting repair patterns.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

//		KeywordFilter nofilter = new KeywordFilter(FilterType.INCLUDE,
//				KeywordType.UNKNOWN, KeywordContext.UNKNOWN, ChangeType.UNKNOWN,
//				"", "");

		KeywordFilter filter = new KeywordFilter(FilterType.INCLUDE,
				KeywordType.RESERVED, KeywordContext.CONDITION, ChangeType.INSERTED,
				"global", "falsey");

		LearningDataSetOptions options = new LearningDataSetOptions();
		CmdLineParser parser = new CmdLineParser(options);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			LearningDataSetMain.printUsage(e.getMessage(), parser);
			return;
		}

		/* Print the help page. */
		if(options.getHelp()) {
			LearningDataSetMain.printHelp(parser);
			return;
		}

		/* Re-construct the data set. */
		LearningDataSet dataSet = new LearningDataSet(options.getDataSetPath(), Arrays.asList(filter));

		/* Print the metrics from the data set. */
//		if(options.getPrintMetrics()) {
//			LearningMetrics metrics = dataSet.getMetrics();
//			for(KeywordFrequency frequency : metrics.changedKeywordFrequency) {
//				System.out.println(frequency.keyword + " : " + frequency.frequency);
//			}
//		}

		/* Pre-process the file. */
		dataSet.preProcess();

		/* Get the clusters. */
		dataSet.getWekaClusters();

		/* Print the data set. */
//		dataSet.writeFilteredDataSet(options.getFilteredPath());

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
