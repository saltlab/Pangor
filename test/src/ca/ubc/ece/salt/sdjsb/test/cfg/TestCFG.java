package ca.ubc.ece.salt.sdjsb.test.cfg;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.sdjsb.cfg.CFGFactory;
import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;
import junit.framework.TestCase;

public class TestCFG extends TestCase {

	@Test
	public void testCFG() throws IOException {
		
		/* Parse the artifact with Rhino. */
		Parser parser = new Parser();
		String file = "./test/input/cfg/basic.js";
		AstRoot root = parser.parse(new FileReader(file), file, 1);
		
		/* Create the CFG. */
		List<CFGNode> cfgs = CFGFactory.createCFGs(root);
		
		/* Print the CFG. */
		int n = 1;
		for(CFGNode cfg : cfgs) {
			System.out.println("CFG" + n + ": " + CFGFactory.printCFG(cfg));
			n++;
		}

	}

}
