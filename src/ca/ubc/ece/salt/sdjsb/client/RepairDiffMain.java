package ca.ubc.ece.salt.sdjsb.client;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ca.ubc.ece.salt.sdjsb.RepairDiff;
import fr.labri.gumtree.client.DiffClient;
import fr.labri.gumtree.client.DiffOptions;

public class RepairDiffMain {
	
	public static void main(String[] args) {
		DiffOptions options = new DiffOptions();
		CmdLineParser parser = new CmdLineParser(options);
		try {
			parser.parseArgument(args);
			DiffClient client;
			if ("repair".equals(options.getOutput())) client = new RepairDiff(options);
			else {
				System.err.println("This main only generates repair diffs.");
				return;
			}
			client.start();
		} catch (CmdLineException e) {
			e.printStackTrace();
		}
	}
	
}