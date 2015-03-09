package ca.ubc.ece.salt.sdjsb.test;

import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ca.ubc.ece.salt.sdjsb.SDJSB;
import ca.ubc.ece.salt.sdjsb.checker.Alert;
import fr.labri.gumtree.client.DiffOptions;
import junit.framework.TestCase;

public abstract class TestSDJSB extends TestCase {
	
	public void runTest(String[] args, List<Alert> expectedAlerts, boolean printAlerts) {

		/* Parse the options. */
		DiffOptions options = new DiffOptions();
		CmdLineParser parser = new CmdLineParser(options);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println("Usage:\nSDJSB /path/to/src /path/to/dst");
			e.printStackTrace();
			return;
		}

		/* Run SDJSB. */
        SDJSB client = new SDJSB(options);
        List<Alert> actualAlerts = client.start();
        
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
