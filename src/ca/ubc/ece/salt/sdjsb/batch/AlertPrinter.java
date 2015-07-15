package ca.ubc.ece.salt.sdjsb.batch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import ca.ubc.ece.salt.sdjsb.alert.Alert;

public class AlertPrinter {
	
	private List<ProjectAnalysisResult> analysisResults;
	
	public AlertPrinter(ProjectAnalysisResult analysisResult ) {
		this.analysisResults = new ArrayList<ProjectAnalysisResult>(1);
		analysisResults.add(analysisResult);
	}
	
	public AlertPrinter(List<ProjectAnalysisResult> analysisResults) {
		this.analysisResults = analysisResults;
	}
	
	/**
	 * Prints a summary of the alerts for each project.
	 * @param printAlerts If true, will print the long description of all alerts.
	 * @throws GitProjectAnalysisException
	 */
	public void printSummary(boolean printAlerts) throws GitProjectAnalysisException {
		
		for(ProjectAnalysisResult analysisResult : this.analysisResults) {
			
			System.out.println("-----------------------------");
			System.out.println("Results for project " + analysisResult.getProjectName() + ":");
			
			for(String type : analysisResult.getTypes()) {
				
				System.out.println("\t" + type + " (" + analysisResult.countAlertsForType(type) + ")");

                for(String subtype : analysisResult.getSubtypesOf(type)) {
                	
                	System.out.println("\t\t" + subtype + "(" + analysisResult.countAlertsForSubType(subtype) + ")");
                	
                	if(printAlerts) {
                		
                		for(Alert alert : analysisResult.getAlertsOfSubType(subtype)) {
                			System.out.println("\t\t\t" + alert.getLongDescription());
                		}
                		
                	}
                	
                }
				
			}

		}

	}
	
	/**
	 * Prints a Latex table of the alerts.
	 */
	public void printLatexTable(){

        System.out.println("-----------------------------");
        System.out.println("LaTeX table for all projects:");

        System.out.println("\\begin{table*}");
        System.out.println("\t\\centering");
        System.out.println("\t\\caption{Results by Project}");
        System.out.println("\t\\begin{tabular}{ | l | l | l | l | l | l | l | l | }");
        System.out.println("\t\t\\hline");
        System.out.println("\t\t\\textbf{Project} & \\textbf{KLoC} & \\textbf{Commits} & \\textbf{Bug Fixing Commits} & \\textbf{Undeclared} & \\textbf{Special Type} & \\textbf{Callback Parameter} & \\textbf{Callback Error} \\\\ \\hline");
		
		for(ProjectAnalysisResult analysisResult : this.analysisResults) {
			
            Integer undeclared_var = analysisResult.countAlertsForType("ND");
            Integer special_type = analysisResult.countAlertsForType("STH");
            Integer callback_parameter = analysisResult.countAlertsForSubType("CB_MISSING_ERROR_PARAMETER");
            Integer callback_error = analysisResult.countAlertsForSubType("CB_UNCHECKED_ERROR_PARAMETER");
        
            System.out.println("\t\t" + analysisResult.getProjectName() + " & [KLoC] & " 
                    + analysisResult.getTotalCommits() + " & " + analysisResult.getBugFixingCommits() 
                    + " & " + undeclared_var 
                    + " & " + special_type 
                    + " & " + callback_parameter 
                    + " & " + callback_error 
                    + " \\\\ \\hline");

		}

        System.out.println("\t\\end{tabular}");
        System.out.println("\t\\label{tbl:projectResults}");
        System.out.println("\\end{table*}");

	}
	
	/**
	 * Prints an alert's feature vector and writes the source code to 
	 * a supplementary file for human inspection.
	 */
	public void printFeatureVector(String outFile, String supplementaryFolder, boolean append) {
		
		PrintStream stream = System.out;
		
		if(outFile != null) {
			try {
				stream = new PrintStream(new FileOutputStream(outFile, append));
			}
			catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}
		
		/* Write the header for the feature vector. */
		if(!append) {
			/* TODO: We need to go through FeatureVectorMagager to print the header. */
			//stream.println(FeatureVector.getHeader());
		}

		/* Write each feature vector from the results. */
		for(ProjectAnalysisResult analysisResult : this.analysisResults) {

			List<Alert> alerts = analysisResult.getAlertsOfSubType("FEATURE_VECTOR_BoW");

			for(Alert alert : alerts) {

				/* The feature vector as a CSV row. */
				stream.println(alert.getFeatureVector(analysisResult.getProjectName(), null, null, null, null));
				
				/* Write out the source and destination code to supplementary files. */
				if(supplementaryFolder != null) {
					
					/* The path to the supplementary folder may not exist. */
					File path = new File(supplementaryFolder);
					path.mkdirs();

					File src = new File(supplementaryFolder, alert.getID() + "_src.js");
					File dst = new File(supplementaryFolder, alert.getID() + "_dst.js");
					
					try (PrintStream srcStream = new PrintStream(new FileOutputStream(src));
						 PrintStream dstStream = new PrintStream(new FileOutputStream(dst));) {

						srcStream.print(alert.getSourceCode());
						dstStream.print(alert.getDestinationCode());
						
						srcStream.close();
						dstStream.close();
						
					} catch (FileNotFoundException e) {
						System.err.println(e.getMessage());
					} 
					
				}

			}

		}

	}
	
}
