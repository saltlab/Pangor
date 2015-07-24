package ca.ubc.ece.salt.sdjsb.learning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import ca.ubc.ece.salt.sdjsb.ControlFlowDifferencing;
import ca.ubc.ece.salt.sdjsb.analysis.learning.ast.FeatureVector;
import ca.ubc.ece.salt.sdjsb.analysis.learning.ast.LearningDataSet;
import ca.ubc.ece.salt.sdjsb.analysis.learning.ast.LearningAnalysis;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisRunner;

public class LearningAnalysisRunner extends AnalysisRunner {

	/** Stores the feature vectors that make up the data set. **/
	LearningDataSet dataset;

	/**
	 * @param packages The list of packages we want to learn repair patterns for.
	 * @param dataSetPath The file path to store the data set.
	 */
	public LearningAnalysisRunner(String dataSetPath, String supplementaryPath) {
		this.dataset = new LearningDataSet(dataSetPath, supplementaryPath);
	}

	@Override
	protected void analyze(ControlFlowDifferencing cfd, AnalysisMetaInformation ami) throws Exception {

		LearningAnalysis analysis = new LearningAnalysis(this.dataset, ami);
		cfd.analyze(analysis);

	}

	/**
	 * @deprecated To preserve memory, analysis results should be written to
	 * 			   disk (in the {@code dataSetPath} location) as they are 
	 * 			   received from the analysis engine. More specifically, the
	 * 			   {@code registerFeatureVector} method of the {@code DataSet}
	 * 			   class should write the feature vector to disk.
	 */
	@Override
	public void printResults(String outFile, String supplementaryFolder) {

		/* Filters out unwanted rows and columns. */
		this.dataset.preProcess();

		/* Open the file stream for writing if a file has been given. */
		PrintStream stream = System.out;

		if(outFile != null) {
			try {
				/*
				 * The path to the output folder may not exist. Create it if
				 * needed.
				 */
				File path = new File(outFile);
				path.getParentFile().mkdirs();

				stream = new PrintStream(new FileOutputStream(outFile));
			}
			catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}

		/* Write the header for the feature vector. */
		stream.println(this.dataset.getFeatureVectorHeader());

		/* Write the data set. */
		stream.println(this.dataset.getFeatureVector());

		/* Write the source code to a folder so we can examine it later. */
		this.printSupplementaryFiles(supplementaryFolder);

	}

	/**
	 * Writes the source code from each of the inspected functions to a file.
	 * @param supplementaryFolder The folder to place the files in.
	 */
	private void printSupplementaryFiles(String supplementaryFolder) {

		if(supplementaryFolder != null) {

			for(FeatureVector featureVector : this.dataset.getFeatureVectors()) {

				/* The path to the supplementary folder may not exist. Create
				 * it if needed. */
				File path = new File(supplementaryFolder);
				path.mkdirs();

				File src = new File(supplementaryFolder, featureVector.id + "_src.js");
				File dst = new File(supplementaryFolder, featureVector.id + "_dst.js");

				try (PrintStream srcStream = new PrintStream(new FileOutputStream(src));
					 PrintStream dstStream = new PrintStream(new FileOutputStream(dst));) {

					srcStream.print(featureVector.sourceCode);
					dstStream.print(featureVector.destinationCode);

					srcStream.close();
					dstStream.close();

				} catch (FileNotFoundException e) {
					System.err.println(e.getMessage());
				}

			}

		}

	}

}
