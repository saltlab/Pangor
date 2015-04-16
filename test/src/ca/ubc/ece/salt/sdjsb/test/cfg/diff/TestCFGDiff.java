package ca.ubc.ece.salt.sdjsb.test.cfg.diff;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.gumtree.ast.ASTClassifier;
import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.sdjsb.cfg.CFG;
import ca.ubc.ece.salt.sdjsb.cfg.CFGFactory;
import ca.ubc.ece.salt.sdjsb.cfg.CFGNode;
import ca.ubc.ece.salt.sdjsb.cfg.CFGPrinter;
import ca.ubc.ece.salt.sdjsb.cfg.CFGPrinter.Output;
import ca.ubc.ece.salt.sdjsb.cfg.diff.CFGDifferencing;
import fr.labri.gumtree.actions.RootAndLeavesClassifier;
import fr.labri.gumtree.actions.TreeClassifier;
import fr.labri.gumtree.gen.js.RhinoTreeGenerator;
import fr.labri.gumtree.matchers.MappingStore;
import fr.labri.gumtree.matchers.Matcher;
import fr.labri.gumtree.matchers.MatcherFactories;
import fr.labri.gumtree.tree.Tree;
import junit.framework.TestCase;

public class TestCFGDiff extends TestCase {

	/**
	 * Integration test for CFG building and GumTree differencing.
	 * @param file
	 * @param expectedCFGs
	 * @param output
	 * @throws IOException
	 */
	protected void runTest(String srcFile, String dstFile, List<String> expectedSrcCFGs, List<String> expectedDstCFGs, Output output) throws IOException {

        /* Create the abstract GumTree representations of the ASTs.
         * 
         * Note: GumTree would use TreeGeneratorRegistry here to build the src
         * and dst trees. However, we're working with the JavaScript AstNodes
         * from the Rhino parser, so we need some language specific info from
         * RhinoTreeGenerator. */

        RhinoTreeGenerator srcRhinoTreeGenerator = new RhinoTreeGenerator();
        RhinoTreeGenerator dstRhinoTreeGenerator = new RhinoTreeGenerator();

        Tree src = srcRhinoTreeGenerator.fromFile(new File(srcFile).getAbsolutePath());
        Tree dst = dstRhinoTreeGenerator.fromFile(new File(dstFile).getAbsolutePath());

		/* Match the source AST nodes to the destination AST nodes. The default
		 * algorithm for doing this is the GumTree algorithm. */
		Matcher matcher = MatcherFactories.newMatcher(src, dst);
		matcher.match();

		/* Classify parts of each tree as deleted, added, moved or updated. The
		 * source tree nodes can be deleted or updated, while the destination
		 * tree nodes can be added, moved or updated. Moved and deleted nodes
		 * are mapped from the source tree to the destination tree. 
		 * 
		 * The classified nodes are stored in hash maps:
		 *  getSrcDeleteTrees() - gets the map containing all delete ops.
		 * 	get[Src|Dst]MvTrees() - gets the map containing all move operations.
		 *  get[Src|Dst]UpdateTrees() - gets the map containing all update ops.
		 *  getDstAddTrees() - gets the map containing all */
		TreeClassifier classifier = new RootAndLeavesClassifier(src, dst, matcher);

		/* We use mapping ids to keep track of mapping changes from the source
		 * to the destination. */
		MappingStore mappings = matcher.getMappings();

		/* Assign the classifications directly to the AstNodes. */
		ASTClassifier astClassifier = new ASTClassifier(src, dst, classifier, mappings);
		astClassifier.classifyASTNodes();

		/* Create the CFGs. */
		List<CFG> srcCFGs = CFGFactory.createCFGs((AstRoot)src.getClassifiedASTNode());
		List<CFG> dstCFGs = CFGFactory.createCFGs((AstRoot)dst.getClassifiedASTNode());
		
		/* Difference the CFGs.
		 * 
		 * We match source and destination CFGs based on the function node
		 * mappings from AST differencing. */
		Map<ClassifiedASTNode, CFG> dstEntryMap = new HashMap<ClassifiedASTNode, CFG>();
		Map<ClassifiedASTNode, CFG> srcEntryMap = new HashMap<ClassifiedASTNode, CFG>();

		for(CFG dstCFG : dstCFGs) {
			dstEntryMap.put(dstCFG.getEntryNode().getStatement(), dstCFG);
		}

		for(CFG srcCFG : srcCFGs) {
			srcEntryMap.put(srcCFG.getEntryNode().getStatement(), srcCFG);
		}

		for(CFG dstCFG : dstCFGs) {
			
			if(srcEntryMap.containsKey(dstCFG.getEntryNode().getStatement().getMapping())) {
				CFG srcCFG = srcEntryMap.get(dstCFG.getEntryNode().getStatement().getMapping());
                CFGDifferencing.computeEdgeChanges(srcCFG, dstCFG);
			}
			
		}

		/* Print the CFGs. */
		System.out.println("Source CFGs: *************");
		this.getCFGs(srcCFGs, output);
		System.out.println("Destination CFGs: *************");
		this.getCFGs(dstCFGs, output);

        /* Check the CFGs. */
		List<String> actualSrcCFGs = new LinkedList<String>();
		for(CFG srcCFG : srcCFGs) {
			actualSrcCFGs.add(CFGPrinter.print(Output.DOT_TEST, srcCFG));
		}
        check(actualSrcCFGs, expectedSrcCFGs);

		List<String> actualDstCFGs = new LinkedList<String>();
		for(CFG dstCFG : dstCFGs) {
			actualDstCFGs.add(CFGPrinter.print(Output.DOT_TEST, dstCFG));
		}
        check(actualDstCFGs, expectedDstCFGs);

	}

	protected void check(List<String> actualCFGs, List<String> expectedCFGs) {
		
		/* Check the CFGs. */
		for(String expectedCFG : expectedCFGs) {
			int index = actualCFGs.indexOf(expectedCFG.replace("\\", "\\\\").replace("\"", "\\\""));
            TestCase.assertTrue("a CFG was not produced correctly", index >= 0);
		}
		
		/* Check that no additional CFGs were produced. */
		TestCase.assertEquals("an unexpected CFG was produced", actualCFGs.size(), expectedCFGs.size());

	}
	
	/**
	 * Prints a list of CFGs from a file.
	 * @param cfgs The CFGs from either the source or destination file.
	 * @param output How to display the output (adjacency list or DOT).
	 */
	private void getCFGs(List<CFG> cfgs, Output output) {

		/* Reset the CFGNode id generator value (needed for consistent test cases). */
		CFGNode.resetIdGen();

		/* Get the serialized CFGs. */
		List<String> actualCFGs = new LinkedList<String>();
        int n = 1;
        for(CFG cfg : cfgs) {
            String serialized = CFGPrinter.adjacencyList(cfg);
            actualCFGs.add(serialized);
            System.out.println("CFG" + n + ": " + CFGPrinter.print(output, cfg));
            n++;
        }
		
	}

	@Test
	public void testIf() throws IOException {
		
		String src = "./test/input/special_type_handling/sth_undefined_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_new.js";
		
		List<String> expectedSrcCFGs = new LinkedList<String>();
		expectedSrcCFGs.add("digraph control_flow_graph { to [ style = filled fillcolor = \"white\" ]; 0 [ fillcolor = \"grey\" label = \"script entry\" ]; 0 -> 2 [ color = \"grey\" ]; 2 [ fillcolor = \"grey\" label = \"var a;\" ]; 2 -> 3 [ color = \"grey\" ]; 3 [ fillcolor = \"yellow\" label = \"console.log(a);\" ]; 3 -> 1 [ color = \"grey\" ]; 1 [ fillcolor = \"grey\" label = \"\" ]; }");
		
		List<String> expectedDstCFGs = new LinkedList<String>();
		expectedDstCFGs.add("digraph control_flow_graph { to [ style = filled fillcolor = \"white\" ]; 4 [ fillcolor = \"grey\" label = \"script entry\" ]; 4 -> 6 [ color = \"grey\" ]; 6 [ fillcolor = \"grey\" label = \"var a;\" ]; 6 -> 7 [ color = \"grey\" ]; 7 [ fillcolor = \"grey\" label = \"\" ]; 7 -> 8 [ color = \"grey\" fontcolor = \"green\" label = \"a !== undefined\" ]; 7 -> 9 [ color = \"green\" fontcolor = \"green\" label = \"!(a !== undefined)\" ]; 8 [ fillcolor = \"yellow\" label = \"console.log(a);\" ]; 8 -> 5 [ color = \"grey\" ]; 9 [ fillcolor = \"grey\" label = \"\" ]; 9 -> 5 [ color = \"green\" ]; 5 [ fillcolor = \"grey\" label = \"\" ]; }");
		
		this.runTest(src, dst, expectedSrcCFGs, expectedDstCFGs, Output.DOT);

	}

	@Test
	public void testReverseIf() throws IOException {
		
		String src = "./test/input/special_type_handling/sth_undefined_new.js";
		String dst = "./test/input/special_type_handling/sth_undefined_old.js";

		List<String> expectedSrcCFGs = new LinkedList<String>();
		expectedSrcCFGs.add("digraph control_flow_graph { to [ style = filled fillcolor = \"white\" ]; 0 [ fillcolor = \"grey\" label = \"script entry\" ]; 0 -> 2 [ color = \"grey\" ]; 2 [ fillcolor = \"grey\" label = \"var a;\" ]; 2 -> 3 [ color = \"grey\" ]; 3 [ fillcolor = \"grey\" label = \"\" ]; 3 -> 4 [ color = \"grey\" fontcolor = \"red\" label = \"a !== undefined\" ]; 3 -> 5 [ color = \"red\" fontcolor = \"red\" label = \"!(a !== undefined)\" ]; 4 [ fillcolor = \"yellow\" label = \"console.log(a);\" ]; 4 -> 1 [ color = \"grey\" ]; 5 [ fillcolor = \"grey\" label = \"\" ]; 5 -> 1 [ color = \"red\" ]; 1 [ fillcolor = \"grey\" label = \"\" ]; }");
		
		List<String> expectedDstCFGs = new LinkedList<String>();
		expectedDstCFGs.add("digraph control_flow_graph { to [ style = filled fillcolor = \"white\" ]; 6 [ fillcolor = \"grey\" label = \"script entry\" ]; 6 -> 8 [ color = \"grey\" ]; 8 [ fillcolor = \"grey\" label = \"var a;\" ]; 8 -> 9 [ color = \"grey\" ]; 9 [ fillcolor = \"yellow\" label = \"console.log(a);\" ]; 9 -> 7 [ color = \"grey\" ]; 7 [ fillcolor = \"grey\" label = \"\" ]; }");
		
		this.runTest(src, dst, expectedSrcCFGs, expectedDstCFGs, Output.DOT);

	}

	@Test
	public void testMultipleCFGs() throws IOException {
		
		String src = "./test/input/callback_parameter/cbp_old.js";
		String dst = "./test/input/callback_parameter/cbp_new.js";

		List<String> expectedSrcCFGs = new LinkedList<String>();
		
		expectedSrcCFGs.add("digraph control_flow_graph { to [ style = filled fillcolor = \"white\" ]; 0 [ fillcolor = \"grey\" label = \"script entry\" ]; 0 -> 2 [ color = \"grey\" ]; 2 [ fillcolor = \"grey\" label = \"printMessage(\\\"Hello World!\\\", donePrinting);\" ]; 2 -> 1 [ color = \"grey\" ]; 1 [ fillcolor = \"grey\" label = \"\" ]; }");
		expectedSrcCFGs.add("digraph control_flow_graph { to [ style = filled fillcolor = \"white\" ]; 3 [ fillcolor = \"grey\" label = \"printMessage(message,callback)\" ]; 3 -> 5 [ color = \"grey\" ]; 5 [ fillcolor = \"grey\" label = \"console.log(message);\" ]; 5 -> 4 [ color = \"grey\" ]; 4 [ fillcolor = \"grey\" label = \"\" ]; }");
		expectedSrcCFGs.add("digraph control_flow_graph { to [ style = filled fillcolor = \"white\" ]; 6 [ fillcolor = \"grey\" label = \"donePrinting()\" ]; 6 -> 8 [ color = \"grey\" ]; 8 [ fillcolor = \"yellow\" label = \"console.log(\\\"Finished!\\\");\" ]; 8 -> 7 [ color = \"grey\" ]; 7 [ fillcolor = \"grey\" label = \"\" ]; }");
		
		List<String> expectedDstCFGs = new LinkedList<String>();
		
		expectedDstCFGs.add("digraph control_flow_graph { to [ style = filled fillcolor = \"white\" ]; 9 [ fillcolor = \"grey\" label = \"script entry\" ]; 9 -> 11 [ color = \"grey\" ]; 11 [ fillcolor = \"grey\" label = \"printMessage(\\\"Hello World!\\\", donePrinting);\" ]; 11 -> 10 [ color = \"grey\" ]; 10 [ fillcolor = \"grey\" label = \"\" ]; }");
		expectedDstCFGs.add("digraph control_flow_graph { to [ style = filled fillcolor = \"white\" ]; 12 [ fillcolor = \"grey\" label = \"printMessage(message,callback)\" ]; 12 -> 14 [ color = \"grey\" ]; 14 [ fillcolor = \"grey\" label = \"console.log(null, message);\" ]; 14 -> 13 [ color = \"grey\" ]; 13 [ fillcolor = \"grey\" label = \"\" ]; }");
		expectedDstCFGs.add("digraph control_flow_graph { to [ style = filled fillcolor = \"white\" ]; 15 [ fillcolor = \"grey\" label = \"donePrinting(err)\" ]; 15 -> 17 [ color = \"grey\" ]; 17 [ fillcolor = \"grey\" label = \"\" ]; 17 -> 18 [ color = \"green\" fontcolor = \"green\" label = \"err\" ]; 17 -> 19 [ color = \"grey\" fontcolor = \"green\" label = \"!(err)\" ]; 18 [ fillcolor = \"green\" label = \"console.log(\\\"Error!\\\");\" ]; 18 -> 20 [ color = \"green\" ]; 19 [ fillcolor = \"grey\" label = \"\" ]; 19 -> 20 [ color = \"grey\" ]; 20 [ fillcolor = \"yellow\" label = \"console.log(\\\"Finished!\\\");\" ]; 20 -> 16 [ color = \"grey\" ]; 16 [ fillcolor = \"grey\" label = \"\" ]; }");
		
		this.runTest(src, dst, expectedSrcCFGs, expectedDstCFGs, Output.DOT);

	}
	
}
