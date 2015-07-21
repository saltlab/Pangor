package ca.ubc.ece.salt.sdjsb.classify;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ca.ubc.ece.salt.sdjsb.CFDTask;
import ca.ubc.ece.salt.sdjsb.ControlFlowDifferencing;
import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisRunner;
import ca.ubc.ece.salt.sdjsb.batch.BatchAlert;
import ca.ubc.ece.salt.sdjsb.batch.ProjectAnalysisResult;

public class ClassifyAnalysisRunner extends AnalysisRunner {
	
	/**
	 * TODO: ProjectAnalysisResult should store the data for all analyses. We need a ProjectAnalysisDataSet.
	 */
	public ClassifyAnalysisRunner() { }

	@Override
	protected void analyze(ControlFlowDifferencing cfd,
			AnalysisMetaInformation ami) throws Exception {
		
		ProjectAnalysisResult analysisResult = new ProjectAnalysisResult(ami.projectID);
        Set<Alert> alerts = new HashSet<Alert>();
        ExecutorService executor = Executors.newSingleThreadExecutor();

        try {
        	List<CFDTask> tasks = new LinkedList<CFDTask>();
        	List<Future<Set<Alert>>> futures = new LinkedList<Future<Set<Alert>>>();
        	
        	/* These analyses are full analyses. */
//        	tasks.add(new CFDTask(cfd, new SpecialTypeAnalysis()));
//        	tasks.add(new CFDTask(cfd, new GlobalToLocalAnalysis()));
//        	tasks.add(new CFDTask(cfd, new CallbackParamAnalysis()));
//        	tasks.add(new CFDTask(cfd, new CallbackErrorAnalysis()));
        	
        	/* These analyses are AST level only. */
//        	tasks.add(new CFDTask(cfd, new STHMetaAnalysis()));
//        	tasks.add(new CFDTask(cfd, new STHScopeAnalysis()));
//        	tasks.add(new CFDTask(cfd, new CBEMetaAnalysis()));
//        	tasks.add(new CFDTask(cfd, new CBEDestinationScopeAnalysis()));
//        	tasks.add(new CFDTask(cfd, new GTLScopeAnalysis()));
        	
        	for(CFDTask task : tasks) {
                futures.add(executor.submit(task));
        	}
        	
        	for(Future<Set<Alert>> future : futures) {
                 alerts.addAll(future.get(10, TimeUnit.SECONDS));
        	}

        }
        catch(TimeoutException e) {
        	System.err.println("Timeout occurred.");
        	throw e;
        }
        catch(Exception e) {
        	throw e;
        }
        finally {
        	executor.shutdownNow();
        }

		for(Alert alert : alerts) {
			analysisResult.insert(new BatchAlert(alert, ami.buggyCommitID, ami.repairedCommitID, ami.buggyFile, ami.repairedFile));
		}
        
	}

	@Override
	public void printResults(String outFile, String supplementaryFolder) {
		// TODO: Print the dataset.
		
	}

}
