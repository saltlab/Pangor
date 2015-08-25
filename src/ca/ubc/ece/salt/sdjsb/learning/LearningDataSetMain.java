package ca.ubc.ece.salt.sdjsb.learning;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.KeywordFilter;
import ca.ubc.ece.salt.sdjsb.analysis.learning.KeywordFilter.FilterType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.LearningDataSet;
import ca.ubc.ece.salt.sdjsb.analysis.learning.LearningMetrics;
import ca.ubc.ece.salt.sdjsb.analysis.learning.LearningMetrics.KeywordFrequency;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordDefinition.KeywordType;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordUse.KeywordContext;
import weka.core.WekaException;

public class LearningDataSetMain {
	protected static final Logger logger = LogManager.getLogger(LearningAnalysisMain.class);

	/**
	 * Creates the learning data sets for extracting repair patterns.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		KeywordFilter nofilter = new KeywordFilter(FilterType.INCLUDE,
				KeywordType.UNKNOWN, KeywordContext.UNKNOWN, ChangeType.UNKNOWN,
				"", "");

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
		LearningDataSet dataSet = new LearningDataSet(options.getDataSetPath(), Arrays.asList(nofilter));

		/* Print the metrics from the data set. */
		if(options.getPrintMetrics()) {
			LearningMetrics metrics = dataSet.getMetrics();
			for(KeywordFrequency frequency : metrics.changedKeywordFrequency) {
				System.out.println(frequency.keyword + " : " + frequency.frequency);
			}
		}

		/* Get the clusters for the data set. */
		if(options.getPrintClusters()) {

			Set<Cluster> clusters = new TreeSet<Cluster>(new Comparator<Cluster>() {
				@Override
				public int compare(Cluster c1, Cluster c2) {
					if(c1.instances == c2.instances) return c1.toString().compareTo(c2.toString());
					else if(c1.instances < c2.instances) return 1;
					else return -1;
				}
			});

			LearningMetrics metrics = dataSet.getMetrics();

			for(KeywordFrequency frequency : metrics.changedKeywordFrequency) {

				/* Build the filter. */
				KeywordFilter clusterFilter = new KeywordFilter(FilterType.INCLUDE,
						frequency.keyword.type, frequency.keyword.context, frequency.keyword.changeType,
						frequency.keyword.apiString, frequency.keyword.keyword);

				/* Re-construct the data set. */
				LearningDataSet clusteringDataSet = new LearningDataSet(options.getDataSetPath(), Arrays.asList(clusterFilter));

				/* Pre-process the file. */
				clusteringDataSet.preProcess();

				/* Get the clusters. */
				try {
					int[] keywordClusters = clusteringDataSet.getWekaClusters();

					for (int i = 0; i < keywordClusters.length; i++) {
						Cluster cluster = new Cluster(frequency.keyword, i, keywordClusters[i]);
						clusters.add(cluster);
					}

					/* Save arff file */
					if (options.getArffFolder() != null)
						clusteringDataSet.writeArffFile(options.getArffFolder(),
								frequency.keyword.toString() + ".arff");
				} catch (WekaException ex) {
					logger.error("Weka error on building clusters.", ex);
				}
			}

			int i = 0;
			for(Cluster cluster : clusters) {
				if (cluster.keyword.context != KeywordContext.STATEMENT
						&& !cluster.keyword.keyword.equals("this")
						&& !cluster.keyword.keyword.equals("typeof")
						&& !cluster.keyword.keyword.equals("falsey")
						&& !cluster.keyword.keyword.equals("undefined")
						&& !cluster.keyword.keyword.equals("null")
						&& !cluster.keyword.keyword.equals("true")
						&& !cluster.keyword.keyword.equals("false")
						&& !cluster.keyword.keyword.equals("test")
						) {
					System.out.println(i + "\t" + cluster);
					i++;
				}
			}
		}

		/* Pre-process the file. */
//		dataSet.preProcess();

		/* Get the clusters. */
//		int[] clusters = dataSet.getWekaClusters();

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
