package ca.ubc.ece.salt.sdjsb;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.mozilla.javascript.ast.AstNode;

import fr.labri.gumtree.actions.RootAndLeavesClassifier;
import fr.labri.gumtree.actions.TreeClassifier;
import fr.labri.gumtree.client.DiffOptions;
import fr.labri.gumtree.gen.js.RhinoTreeGenerator;
import fr.labri.gumtree.matchers.MappingStore;
import fr.labri.gumtree.matchers.Matcher;
import fr.labri.gumtree.matchers.MatcherFactories;
import fr.labri.gumtree.tree.Tree;
import ca.ubc.ece.salt.gumtree.ast.ASTClassifier;
import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.checker.CheckerContext;
import ca.ubc.ece.salt.sdjsb.checker.CheckerRegistry;

public class SDJSB  {
	
	/**
	 * The main entry point for command line executions of SDJSB.
	 * @param args SDJSB /path/to/src /path/to/dst
	 */
	public static void main(String[] args) {
		DiffOptions options = new DiffOptions();
		CmdLineParser parser = new CmdLineParser(options);

		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println("Usage:\nSDJSB /path/to/src /path/to/dst");
			e.printStackTrace();
			return;
		}
		
        List<Alert> alerts = SDJSB.analyze(options);

        System.out.println("Alerts:");
        for(Alert alert : alerts){
            System.out.println("\t" + alert.getLongDescription());
        }
	}

	/**
	 * Analyze the files given in the options and use the default checkers.
	 */
	public static List<Alert> analyze(DiffOptions options) {
		return SDJSB.analyze(options, null, null);
	}

	/**
	 * Analyze the files and use the default checkers.
	 */
	public static List<Alert> analyze(DiffOptions options, String sourceFile, String destinationFile) {
		List<String> checkers = new LinkedList<String>();
		checkers.add("ca.ubc.ece.salt.sdjsb.checker.specialtype.SpecialTypeChecker"); // OK (50%)
		//checkers.add("ca.ubc.ece.salt.sdjsb.checker.doesnotexist.DoesNotExistChecker"); // DO NOT USE (0%)
		checkers.add("ca.ubc.ece.salt.sdjsb.checker.notdefined.NotDefinedChecker"); // OK (100%)
		checkers.add("ca.ubc.ece.salt.sdjsb.checker.callbackparam.CallbackParameterChecker"); // OK (63%)
		checkers.add("ca.ubc.ece.salt.sdjsb.checker.callbackerror.CallbackErrorChecker"); // OK (78%)
		return SDJSB.analyze(options,  checkers, sourceFile, destinationFile);
	}

	/**
	 * Analyze the files given in the options with the given checkers.
	 */
	public static List<Alert> analyze(DiffOptions options, List<String> checkers) {
		return SDJSB.analyze(options,  checkers, null, null);
	}

	/**
	 * Analyze the files with the given checkers.
	 */
	public static List<Alert> analyze(DiffOptions options, List<String> checkers, String sourceFile, String destinationFile) {

        /* Create the abstract GumTree representations of the ASTs.
         * 
         * Note: GumTree would use TreeGeneratorRegistry here to build the src
         * and dst trees. However, we're working with the JavaScript AstNodes
         * from the Rhino parser, so we need some language specific info from
         * RhinoTreeGenerator. */
        Tree src;
        Tree dst;
        Map<AstNode, Tree> srcTreeNodeMap;
        Map<AstNode, Tree> dstTreeNodeMap;
        RhinoTreeGenerator srcRhinoTreeGenerator = new RhinoTreeGenerator();
        RhinoTreeGenerator dstRhinoTreeGenerator = new RhinoTreeGenerator();

        try{
        	
        	/* If we are given the files as a string, use them. Otherwise, get
        	 * the files from the file system. */

        	if(sourceFile == null) {
                File fSrc = new File(options.getSrc());
                src = srcRhinoTreeGenerator.fromFile(fSrc.getAbsolutePath());
        	} else {
                src = srcRhinoTreeGenerator.fromSource(sourceFile, options.getSrc());
        	}

        	if(destinationFile == null) {
                File fDst = new File(options.getDst());
                dst = dstRhinoTreeGenerator.fromFile(fDst.getAbsolutePath());
        	} else {
                dst = dstRhinoTreeGenerator.fromSource(destinationFile, options.getDst());
        	}

            srcTreeNodeMap = srcRhinoTreeGenerator.getTreeNodeMap();
            dstTreeNodeMap = dstRhinoTreeGenerator.getTreeNodeMap();

        } catch (IOException e) {
        	System.err.println(e.getMessage());
        	return null;
        }
		
		/* Match the source AST nodes to the destination AST nodes. The default
		 * algorithm for doing this is the GumTree algorithm. */
		Matcher matcher = MatcherFactories.newMatcher(src, dst);
		matcher.match();
		
		/* Produce the diff object that we will use to infer properties of
		 * repairs. */
		try{
            return SDJSB.produce(checkers, src, dst, srcTreeNodeMap, dstTreeNodeMap, matcher);
		} catch (IOException e) {
        	System.err.println(e.getMessage());
        	return null;
		}

	}
	
	/**
	 * Computes the set of changes that transforms the source AST into the
	 * destination AST. Each change is one of {delete, add, move, update}.
	 * @param src The GumTree AST generated for the source file.
	 * @param dst The GumTree AST generated for the destination file.
	 * @param matcher The set of source nodes matched to destination nodes.
	 * @throws IOException
	 */
	private static List<Alert> produce(List<String> checkers, Tree src, Tree dst, Map<AstNode, Tree> srcTreeNodeMap, Map<AstNode, Tree> dstTreeNodeMap, Matcher matcher) throws IOException {
		
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
		TreeClassifier c = new RootAndLeavesClassifier(src, dst, matcher);

		/* We use mapping ids to keep track of mapping changes from the source
		 * to the destination. */
		MappingStore mappings = matcher.getMappings();
		
		/* Assign the classifications directly to the AstNodes. */
		ASTClassifier astClassifier = new ASTClassifier(src, dst, c, mappings);
		astClassifier.classifyASTNodes();

		/* Create the 'event bus' for the repair checkers. */
		CheckerContext checkerContext = new CheckerContext(src, dst, srcTreeNodeMap, dstTreeNodeMap, c, mappings);
		CheckerRegistry checkerRegistry = new CheckerRegistry(checkerContext);
		
		/* Register the checkers. */
		for(String checker : checkers) {
			try {
                checkerRegistry.register(checker);
			} catch (Exception e) {
				System.out.println("WARNING: Unable to register checker '" + checker + "'.");
			}
		}
		
		/* Run the analysis. */
		checkerRegistry.analyze();

		/* Return the alerts. */
		return checkerRegistry.getAlerts();
		
	}

}