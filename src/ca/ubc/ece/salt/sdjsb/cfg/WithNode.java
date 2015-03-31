package ca.ubc.ece.salt.sdjsb.cfg;

import org.mozilla.javascript.ast.AstNode;

/**
 * A CFGNode that adds the given expression to the scope in the following block.
 * @author qhanam
 */
public class WithNode extends CFGNode {
	
	private CFGNode scopeBlock;
	public CFGNode mergeNode;
	
	public WithNode(AstNode statement) {
		super(statement);
	}
	
	public void setScopeBlock(CFGNode trueBranch) {
		this.scopeBlock = trueBranch;
	}
	
	public CFGNode getScopeBlock() {
		return this.scopeBlock;
	}

	@Override
	public void mergeInto(CFGNode nextNode) {
		
		/* If either of the branches is null, there is no subgraph for that
		 * path, so we merge this node directly into the next node. */
		
		this.mergeNode = nextNode;
		
		if(this.scopeBlock == null) {
			this.scopeBlock = nextNode;
		}

	}

	@Override
	public String printSubGraph(CFGNode mergeNode) {

        String s;
        
        if(this.getScopeBlock() != this.mergeNode) {
            /* There is a block to print. */
            s = "WITH(" + this.toString() + ")" + "{" + this.getScopeBlock().printSubGraph(this.mergeNode) + "}";
        }
        else {
        	/* There is not inner block to print. */
            s = this.toString();
        }

        if(mergeNode == this.mergeNode) {
            /* We are not at the bottom level of the merge. */
            return s;
        }

        /* We are at the bottom level of the merge. */
        String subGraph = this.mergeNode.printSubGraph(mergeNode);
        if(subGraph.charAt(0) == '}') {
            return s + subGraph;
        }
        return s + "->" + subGraph;

	}

}
