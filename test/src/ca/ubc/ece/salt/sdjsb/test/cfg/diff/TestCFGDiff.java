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
	protected void runTest(String srcFile, String dstFile, Output output) throws IOException {

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
                //CFGDifferencing.computeEdgeChanges(srcCFGs.get(0), dstCFGs.get(0));
			}
			
		}

		/* Print the CFGs. */
		System.out.println("Source CFGs: *************");
		this.getCFGs(srcCFGs, output);
		System.out.println("Destination CFGs: *************");
		this.getCFGs(dstCFGs, output);
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
		this.runTest(src, dst, Output.DOT);

	}

	@Test
	public void testReverseIf() throws IOException {
		
		String src = "./test/input/special_type_handling/sth_undefined_new.js";
		String dst = "./test/input/special_type_handling/sth_undefined_old.js";
		this.runTest(src, dst, Output.DOT);

	}

	@Test
	public void testMultipleCFGs() throws IOException {
		
		String src = "./test/input/callback_parameter/cbp_old.js";
		String dst = "./test/input/callback_parameter/cbp_new.js";
		this.runTest(src, dst, Output.DOT);

	}
	

}
