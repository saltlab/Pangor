package ca.ubc.ece.salt.sdjsb.test.cfg;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.sdjsb.cfg.CFG;
import ca.ubc.ece.salt.sdjsb.cfg.CFGFactory;
import ca.ubc.ece.salt.sdjsb.cfg.CFGPrinter;
import junit.framework.TestCase;

public class TestCFG extends TestCase {

	@Test
	public void testCFG() throws IOException {
		
		/* Parse the artifact with Rhino. */
		Parser parser = new Parser();
		String file = "./test/input/cfg/basic.js";
		AstRoot root = parser.parse(new FileReader(file), file, 1);
		
		/* Create the CFG. */
		List<CFG> cfgs = CFGFactory.createCFGs(root);
		
		/* Print the CFG. */
		int n = 1;
		for(CFG cfg : cfgs) {
            CFGPrinter printer = new CFGPrinter(cfg);
			System.out.println("CFG" + n + ": " + printer.print());
			n++;
		}

	}

}
