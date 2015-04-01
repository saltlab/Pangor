package ca.ubc.ece.salt.sdjsb.cfg;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;

/**
 * A CFGNode that has two exit edges: one for the non-error condition and
 * one for when an error is raised in the body of the try statement. 
 * 
 * While this representation misses the execution that happens in the try block
 * before an error is thrown, it at least allows us to investigate the error
 * path without knowing which statements throw exceptions (which for JavaScript
 * requires data flow analysis).
 * 
 * TODO: Try statements have some other tricky behavior we need to capture or ignore...
 * 		 such as executing the statements in the finally block before executing
 * 		 return, throw, break or continue nodes in the try or catch blocks.
 * @author qhanam
 */
public class TryNode extends CFGNode {
	
	private CFGNode tryBranch;
	private Map<AstNode, CFGNode> catchBranches;
	private CFGNode finallyBranch;

	public CFGNode mergeNode;
	
	public TryNode(AstNode statement) {
		super(statement);
		this.catchBranches = new HashMap<AstNode, CFGNode>();
	}
	
	public void setTryBranch(CFGNode tryBranch) {
		this.tryBranch = tryBranch;
	}
	
	public CFGNode getTryBranch() {
		return this.tryBranch;
	}

	public void addCatchClause(AstNode exception, CFGNode catchBranch) {
		this.catchBranches.put(exception, catchBranch);
	}

	public Set<AstNode> getCatchClauses() {
		return this.catchBranches.keySet();
	}
	
	public CFGNode getCatchClause(AstNode exception) {
		return this.catchBranches.get(exception);
	}
	
	public void setFinallyBranch(CFGNode finallyBranch) {
		this.finallyBranch = finallyBranch;
	}
	
	public CFGNode getFinallyBranch() {
		return this.finallyBranch;
	}
	
	@Override
	public void mergeInto(CFGNode nextNode) {
		
		this.mergeNode = nextNode;
		
	}

	@Override
	public String printSubGraph(CFGNode mergeNode) {

        String s;

        s = this.toString() + "?{" + this.getTryBranch().printSubGraph(this.finallyBranch);

        Set<AstNode> exceptions = this.getCatchClauses();
            
        for(AstNode exception : exceptions) {
        	
        	CFGNode catchBranch = this.getCatchClause(exception);
        	s += ",catch:" + catchBranch.printSubGraph(this.getFinallyBranch());
        	
        }
        
        s += ",finally:" + this.getFinallyBranch().printSubGraph(this.mergeNode) + "}";
        
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
