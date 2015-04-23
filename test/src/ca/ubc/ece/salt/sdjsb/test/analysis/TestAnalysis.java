package ca.ubc.ece.salt.sdjsb.test.analysis;

import java.util.List;

import ca.ubc.ece.salt.sdjsb.ControlFlowDifferencing;
import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.analysis.FlowAnalysis;
import junit.framework.TestCase;

public class TestAnalysis extends TestCase {

	/**
	 * Tests flow analysis repair classifiers.
	 * @param args The command line arguments (i.e., old and new file names).
	 * @param expectedAlerts The list of alerts that should be produced.
	 * @param printAlerts If true, print the alerts to standard output.
	 * @throws Exception 
	 */
	protected void runTest(String[] args, List<Alert> expectedAlerts, boolean printAlerts, FlowAnalysis<?> analysis) throws Exception {
        
		/* Run the analysis. */
        List<Alert> actualAlerts = ControlFlowDifferencing.analyze(args, analysis);

        /* Output if needed. */
        if(printAlerts) {
        	for(Alert alert : actualAlerts) {
        		System.out.println(alert.getLongDescription());
        	}
        }

		/* Check the output. */
        this.check(actualAlerts, expectedAlerts);

	}
	
	protected void check(List<Alert> actualAlerts, List<Alert> expectedAlerts) {
		/* Check that all the expected alerts are produced by SDJSB. */
		for(Alert expected : expectedAlerts) {
			assertTrue("SDJSB did not produce the alert \"" + expected.getLongDescription() + "\"", actualAlerts.contains(expected));
		}
		
		/* Check that only the expected alerts are produced by SDJSB. */
		for(Alert actual : actualAlerts) {
			assertTrue("SDJSB produced the unexpected alert \"" + actual.getLongDescription() + "\"", expectedAlerts.contains(actual));
		}
	}
	
}