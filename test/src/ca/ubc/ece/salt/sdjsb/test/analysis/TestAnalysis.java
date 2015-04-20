package ca.ubc.ece.salt.sdjsb.test.analysis;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.mozilla.javascript.ast.AstRoot;

import ca.ubc.ece.salt.gumtree.ast.ASTClassifier;
import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.analysis.FlowAnalysis;
import ca.ubc.ece.salt.sdjsb.cfg.CFG;
import ca.ubc.ece.salt.sdjsb.cfg.CFGFactory;
import ca.ubc.ece.salt.sdjsb.cfg.diff.CFGDifferencing;
import fr.labri.gumtree.actions.RootAndLeavesClassifier;
import fr.labri.gumtree.actions.TreeClassifier;
import fr.labri.gumtree.client.DiffOptions;
import fr.labri.gumtree.gen.js.RhinoTreeGenerator;
import fr.labri.gumtree.matchers.MappingStore;
import fr.labri.gumtree.matchers.Matcher;
import fr.labri.gumtree.matchers.MatcherFactories;
import fr.labri.gumtree.tree.Tree;
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

        /* Create the abstract GumTree representations of the ASTs.
         * 
         * Note: GumTree would use TreeGeneratorRegistry here to build the src
         * and dst trees. However, we're working with the JavaScript AstNodes
         * from the Rhino parser, so we need some language specific info from
         * RhinoTreeGenerator. */

        RhinoTreeGenerator srcRhinoTreeGenerator = new RhinoTreeGenerator();
        RhinoTreeGenerator dstRhinoTreeGenerator = new RhinoTreeGenerator();

        Tree src = srcRhinoTreeGenerator.fromFile(new File(options.getSrc()).getAbsolutePath());
        Tree dst = dstRhinoTreeGenerator.fromFile(new File(options.getDst()).getAbsolutePath());

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
		
		/* Run the analysis. */
		AstRoot script = (AstRoot)dst.getClassifiedASTNode();
        analysis.analyze(script, dstCFGs);
        List<Alert> actualAlerts = analysis.getAlerts();

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