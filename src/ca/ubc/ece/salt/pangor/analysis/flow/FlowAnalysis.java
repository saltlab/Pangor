package ca.ubc.ece.salt.pangor.analysis.flow;

import java.util.List;

import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.pangor.analysis.Alert;
import ca.ubc.ece.salt.pangor.analysis.DataSet;
import ca.ubc.ece.salt.pangor.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.analysis.scope.ScopeAnalysis;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.cfg.CFG;
import ca.ubc.ece.salt.pangor.cfg.CFGEdge;
import ca.ubc.ece.salt.pangor.cfg.CFGNode;

/**
 * Performs a change-sensitive, intra-procedural analysis.
 *
 * This framework provides the analysis framework and the current scope.
 *
 * Loops are executed once.
 *
 * @param <LE> The lattice element type that stores the analysis information.
 */
public abstract class FlowAnalysis<U extends Alert, T extends DataSet<U>, LE extends AbstractLatticeElement> extends ScopeAnalysis<U, T> {

	public FlowAnalysis(T dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami);
	}

	/**
	 * Performs a flow analysis.
	 *
	 * For JavaScript, we need to store the function-hierarchy so we can analyze
	 * them in-order and build the scope for each function.is of the script.
	 *
	 * @param root The root AST node for the script.
	 * @param cfgs The control flow graphs for the functions (and script).
	 * @throws Exception
	 */
	@Override
	public void analyze(AstRoot root, List<CFG> cfgs) throws Exception {

		/* Build the scope. */
		super.analyze(root, cfgs);

		/* Analyze the file. */
		this.analyze(this.dstScope, this.dstCFGs);

	}

	/**
	 * Performs a flow analysis.
	 *
	 * For JavaScript, we need to store the function-hierarchy so we can analyze
	 * them in-order and build the scope for each function.is of the script.
	 *
	 * @param dstRoot The root AST node for the script.
	 * @param dstCFGs The control flow graphs for the functions (and script).
	 * @throws Exception
	 */
	@Override
	public void analyze(AstRoot srcRoot, List<CFG> srcCFGs, AstRoot dstRoot, List<CFG> dstCFGs) throws Exception {

		/* Build the scope. */
		super.analyze(srcRoot, srcCFGs, dstRoot, dstCFGs);

		/* Analyze the destination file. */
		this.analyze(this.dstScope, this.dstCFGs);
		this.analyze(this.srcScope, this.srcCFGs);

	}

	/**
	 * Perform a path-sensitive analysis.
	 *
	 * As we discover variable declarations, we add them to the scope.
	 *
	 * @param cfg the control flow graph for the function or script we are analyzing.
	 * @param scopeStack the scope for the function.
	 */
	abstract protected void analyze(CFG cfg, Scope scope);

	/**
	 * @param function The function under analysis.
	 * @return an initialized lattice element for the function.
	 */
	public abstract LE entryValue(ScriptNode function);

	/**
	 * Transfer the lattice element over the CFGEdge.
	 * @param edge The edge to transfer over.
	 */
	public abstract void transfer(CFGEdge edge, LE sourceLE, Scope scope);

	/**
	 * Transfer the lattice element over the CFGNode.
	 * @param node The node to transfer over.
	 */
	public abstract void transfer(CFGNode node, LE sourceLE, Scope scope);

	/**
	 * @param le The lattice element to copy.
	 * @return a deep copy of the lattice element.
	 */
	public abstract LE copy(LE le);

	/**
	 * Discovers functions and sends them to be analyzed. Keeps track of
	 * function scopes to be passed to sub-functions.
	 * @throws Exception
	 */
	private void analyze(Scope scope, List<CFG> cfgs) throws Exception {

        /* Analyze node. */

        CFG cfg = this.getFunctionCFG(scope.scope, cfgs);
        if(cfg == null) throw new Exception("CFG not found for function.");
        this.setCurrentCFGIdentity(scope.identity);
        this.analyze(cfg, scope);

        /* Analyze the methods of the function. */

        for(Scope childScope : scope.children){
        	analyze(childScope, cfgs);
        }

	}

	/**
	 * Find the CFG for the script or function.
	 * @param node a AstRoot or FunctionNode
	 * @return the CFG for the script or function.
	 */
	private CFG getFunctionCFG(ScriptNode node, List<CFG> cfgs) {

		for(CFG cfg : cfgs) {
			if(cfg.getEntryNode().getStatement() == node) return cfg;
		}

		return null;

	}

}
