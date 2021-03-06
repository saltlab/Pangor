package ca.ubc.ece.salt.pangor.cfd;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import ca.ubc.ece.salt.gumtree.ast.ASTClassifier;
import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.analysis.Analysis;
import ca.ubc.ece.salt.pangor.cfg.CFG;
import ca.ubc.ece.salt.pangor.cfg.CFGFactory;
import ca.ubc.ece.salt.pangor.cfg.diff.CFGDifferencing;
import fr.labri.gumtree.actions.RootAndLeavesClassifier;
import fr.labri.gumtree.actions.TreeClassifier;
import fr.labri.gumtree.client.DiffOptions;
import fr.labri.gumtree.gen.js.RhinoTreeGenerator;
import fr.labri.gumtree.matchers.MappingStore;
import fr.labri.gumtree.matchers.Matcher;
import fr.labri.gumtree.matchers.MatcherFactories;
import fr.labri.gumtree.tree.Tree;

/**
 * A control class for performing control flow differencing and running a flow
 * analysis on a source and destination file.
 */
public class ControlFlowDifferencing {

	/** Stores the CFG and AST for analysis. **/
	private CFDContext context;

	/**
	 * Creates the analysis context by control flow differencing the source
	 * and destination files (provided as a string).
	 * @param cfgFactory The factory class that builds the CFGs.
	 * @param args The command line options (contains the paths to the source
	 * 			   and destination files to difference).
	 * @throws Exception thrown when a problem occurs during control flow differencing.
	 */
	public ControlFlowDifferencing(CFGFactory cfgFactory, String[] args) throws Exception {
		this(cfgFactory, args, null, null);
	}

	/**
	 * Creates the analysis context by control flow differencing the source
	 * and destination files (provided as a string).
	 * @param cfgFactory The factory class that builds the CFGs.
	 * @param args The command line options (contains the paths to the source
	 * @param args The analysis/differencing options.
	 * @param srcSourceCode The source file as a string.
	 * @param dstSourceCode The destination file as a string.
	 * @throws Exception thrown when a problem occurs during control flow differencing.
	 */
	public ControlFlowDifferencing(CFGFactory cfgFactory, String[] args, String srcSourceCode, String dstSourceCode) throws Exception {

		/* Get the analysis options. */
		DiffOptions options = ControlFlowDifferencing.getAnalysisOptions(args);

		/* Set up the analysis context. */
		this.context =  ControlFlowDifferencing.setup(cfgFactory, options, srcSourceCode, dstSourceCode);

	}

	/**
	 * Perform a control flow differencing analysis.
	 * @param analysis The analysis to run.
	 * @return The list of alerts from the analysis.
	 * @throws Exception
	 */
	public void analyze(Analysis<?, ?> analysis) throws Exception {

		/* Perform the analysis. */
        analysis.analyze(this.context.srcScript, this.context.srcCFGs, this.context.dstScript, this.context.dstCFGs);

	}

	/**
	 * Compute the control flow changes.
	 * @param args The command line analysis arguments.
	 * @return The context for a control flow differencing analysis.
	 * @throws Exception
	 */
	public static CFDContext setup(CFGFactory cfgFactory, String[] args) throws Exception {

		/* Get the analysis options. */
		DiffOptions options = ControlFlowDifferencing.getAnalysisOptions(args);

		/* Set up the analysis context. */
		return ControlFlowDifferencing.setup(cfgFactory, options);

	}

	/**
	 * Compute the control flow changes.
	 * @param options The command line analysis options.
	 * @return The context for a control flow differencing analysis.
	 * @throws Exception
	 */
	public static CFDContext setup(CFGFactory cfgFactory, DiffOptions options) throws Exception {
		return setup(cfgFactory, options, null, null);
	}

	/**
	 * Compute the control flow changes.
	 * @param options The command line analysis options.
	 * @return The context for a control flow differencing analysis.
	 * @throws Exception
	 */
	public static CFDContext setup(CFGFactory cfgFactory, DiffOptions options, String srcSourceCode, String dstSourceCode) throws Exception {

        /* Create the abstract GumTree representations of the ASTs. */
        Tree src = null;
        Tree dst = null;
        if(srcSourceCode == null) src = ControlFlowDifferencing.createGumTree(options.getSrc(), options.getPreProcess());
        else src = ControlFlowDifferencing.createGumTree(srcSourceCode, options.getSrc(), options.getPreProcess());
        if(dstSourceCode == null) dst = ControlFlowDifferencing.createGumTree(options.getDst(), options.getPreProcess());
        else dst = ControlFlowDifferencing.createGumTree(dstSourceCode, options.getDst(), options.getPreProcess());

		/* Match the source tree nodes to the destination tree nodes. */
        Matcher matcher = ControlFlowDifferencing.matchTreeNodes(src, dst);

        /* Apply change classifications to nodes in the GumTrees. */
        ControlFlowDifferencing.classifyTreeNodes(src, dst, matcher);

		/* Create the CFGs. */
		List<CFG> srcCFGs = cfgFactory.createCFGs(src.getClassifiedASTNode());
		List<CFG> dstCFGs = cfgFactory.createCFGs(dst.getClassifiedASTNode());

		/* Compute changes to CFG elements (nodes, edges and edge labels). */
		ControlFlowDifferencing.computeCFGChanges(srcCFGs, dstCFGs);

		/* Return the set up results (the context for a CFD analysis) */
		ClassifiedASTNode srcRoot = src.getClassifiedASTNode();
		ClassifiedASTNode dstRoot = dst.getClassifiedASTNode();
		return new CFDContext(srcRoot, dstRoot, srcCFGs, dstCFGs);

	}

	/**
	 * Parse the analysis options.
	 * @param args Command line arguments like source and destination file paths.
	 * @return The DiffOptions file for the analysis.
	 * @throws CmdLineException Indicates some required arguments are missing
	 * 							or malformed.
	 */
	public static DiffOptions getAnalysisOptions(String[] args) throws CmdLineException {

		/* Parse the options. */
		DiffOptions options = new DiffOptions();
		CmdLineParser parser = new CmdLineParser(options);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			throw new CmdLineException(parser, "Usage:\ncfdiff /path/to/src /path/to/dst");
		}

		return options;

	}

	/**
	 * Create the abstract GumTree representation of the ASTs.
	 *
	 * Note: GumTree would use TreeGeneratorRegistry here to build the src
     * and dst trees. However, we're working with the JavaScript AstNodes
     * from the Rhino parser, so we need some language specific info from
     * RhinoTreeGenerator.
	 *
	 * @param file The file containing the source code.
	 * @param preProcess Set to true to perform pre-processing on the AST.
	 * @return The GumTree (AST) representation of the source file.
	 * @throws IOException When something goes wrong reading the source file.
	 */
	public static Tree createGumTree(String path, boolean preProcess) throws IOException {

        RhinoTreeGenerator rhinoTreeGenerator = new RhinoTreeGenerator();
        Tree tree = rhinoTreeGenerator.fromFile(new File(path).getAbsolutePath(), preProcess);
        return tree;

	}

	/**
	 * Create the abstract GumTree representation of the ASTs.
	 *
	 * Note: GumTree would use TreeGeneratorRegistry here to build the src
     * and dst trees. However, we're working with the JavaScript AstNodes
     * from the Rhino parser, so we need some language specific info from
     * RhinoTreeGenerator.
	 *
	 * @param file The file containing the source code.
	 * @param preProcess Set to true to perform pre-processing on the AST.
	 * @return The GumTree (AST) representation of the source file.
	 * @throws IOException When something goes wrong reading the source file.
	 */
	public static Tree createGumTree(String source, String path, boolean preProcess) throws IOException {

        RhinoTreeGenerator rhinoTreeGenerator = new RhinoTreeGenerator();
        Tree tree = rhinoTreeGenerator.fromSource(source, path, preProcess);
        return tree;

	}

	/**
	 * Match the source Tree (AST) nodes to the destination nodes.
	 *
	 * The default algorithm for doing this is the GumTree algorithm (used
	 * here), but other methods (like ChangeDistiller) could also be used
	 * with a bit more instrumentation.
	 * @param src The source GumTree (AST).
	 * @param dst The destination GumTree (AST).
	 * @return The data structure containing GumTree node mappings.
	 */
	public static Matcher matchTreeNodes(Tree src, Tree dst) {
		Matcher matcher = MatcherFactories.newMatcher(src, dst);
		matcher.match();
		return matcher;
	}

	/**
	 * Classify nodes in the source and destination trees as deleted, added,
	 * moved or updated. The source tree nodes can be deleted, moved or updated,
	 * while the destination tree nodes can be inserted, moved or updated.
	 * Moved, deleted and unchanged nodes have mappings from the source tree to
	 * the destination tree.
	 * @param src The source GumTree (AST).
	 * @param dst The destination GumTree (AST).
	 * @param matcher The data structure containing GumTree node mappings.
	 * @throws InvalidClassException If GumTree Tree nodes are generated from a parser other than Mozilla Rhino.
	 */
	public static void classifyTreeNodes(Tree src, Tree dst, Matcher matcher) throws InvalidClassException {

		/* Classify the GumTree (Tree) nodes. */
		TreeClassifier classifier = new RootAndLeavesClassifier(src, dst, matcher);

		/* We use mapping ids to keep track of mapping changes from the source
		 * to the destination. */
		MappingStore mappings = matcher.getMappings();

		/* Assign the classifications directly to the AstNodes. */
		ASTClassifier astClassifier = new ASTClassifier(src, dst, classifier, mappings);
		astClassifier.classifyASTNodes();

	}

	/**
	 * Compute changes to CFG elements (nodes, edges and edge labels).
	 *
	 * CFG source and destination nodes are mapped based on their AST mappings.
	 * @param srcCFGs The list of source CFGs.
	 * @param dstCFGs The list of destination CFGs.
	 */
	public static void computeCFGChanges(List<CFG> srcCFGs, List<CFG> dstCFGs) {

		/* Map source CFG nodes to destination CFG nodes. */
		Map<ClassifiedASTNode, CFG> dstEntryMap = new HashMap<ClassifiedASTNode, CFG>();
		Map<ClassifiedASTNode, CFG> srcEntryMap = new HashMap<ClassifiedASTNode, CFG>();

		for(CFG dstCFG : dstCFGs) {
			dstEntryMap.put(dstCFG.getEntryNode().getStatement(), dstCFG);
		}

		for(CFG srcCFG : srcCFGs) {
			srcEntryMap.put(srcCFG.getEntryNode().getStatement(), srcCFG);
		}

		/* Compute edge changes. */
		for(CFG dstCFG : dstCFGs) {

			if(srcEntryMap.containsKey(dstCFG.getEntryNode().getStatement().getMapping())) {
				CFG srcCFG = srcEntryMap.get(dstCFG.getEntryNode().getStatement().getMapping());
                CFGDifferencing.computeEdgeChanges(srcCFG, dstCFG);
			}

		}

	}

}
