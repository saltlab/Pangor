package ca.ubc.ece.salt.sdjsb.batch;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * TaskRunner wrapper for GitProjectAnalysis. Initially GitProjectAnalysis would
 * implement Callable itself, but because of the use of a latch, a new class was
 * created.
 */
public class GitProjectAnalysisTask implements Callable<Void> {
	private GitProjectAnalysis gitProjectAnalysis;
	private CountDownLatch latch;

	public GitProjectAnalysisTask(GitProjectAnalysis gitProjectAnalysis, CountDownLatch latch) {
		this.gitProjectAnalysis = gitProjectAnalysis;
		this.latch = latch;
	}

	@Override
	public Void call() throws Exception {
		try {
			gitProjectAnalysis.analyze();
		} catch (Exception e) {
			System.err.println("[ERR] Exception on GitProjectAnalysisTask");
			e.printStackTrace();
		} finally {
			latch.countDown();
		}

		return null;
	}

}
