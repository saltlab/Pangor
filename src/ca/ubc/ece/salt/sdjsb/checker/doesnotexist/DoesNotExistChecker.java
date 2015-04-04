package ca.ubc.ece.salt.sdjsb.checker.doesnotexist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.gumtree.ast.ParserASTNode;
import ca.ubc.ece.salt.sdjsb.checker.AbstractChecker;
import ca.ubc.ece.salt.sdjsb.checker.CheckerContext;
import ca.ubc.ece.salt.sdjsb.checker.CheckerContext.ChangeType;
import ca.ubc.ece.salt.sdjsb.checker.CheckerUtilities;
import ca.ubc.ece.salt.sdjsb.checker.doesnotexist.DoesNotExistCheckerUtilities.NameType;

/**
 * Detects repairs that fix fields that do not exist.
 * 
 * For example, if a repair modifies a field name in a comparison, without
 * modifying that field name in an assignment, it indicates that the wrong
 * field name was being dereferenced. 
 * 
 * @author qhanam
 */
public class DoesNotExistChecker extends AbstractChecker {
	
	private static final String TYPE = "DNE";
	
	/**
	 * Keeps track of fields that have been partially modified.
	 */
	private Map<String, DNESpecs> uses;
	
	/**
	 * Keeps track of fields that have been assigned.
	 */
	private Map<String, DNESpecs> assignments;

	/**
	 * The set of identifiers present in the destination file.
	 */
	private Set<String> identifiersInDestination;
	
	public DoesNotExistChecker(CheckerContext context) {
		super(context);
		this.uses = new HashMap<String, DNESpecs>();
		this.assignments = new HashMap<String, DNESpecs>();
	}

	@Override
	public void sourceDelete(AstNode node) { 
        checkSourceName(node);
	}

	@Override
	public void sourceUpdate(AstNode node) { 
        checkSourceName(node);
    }

	@Override
	public void sourceMove(AstNode node) { return; }

	@Override
	public void destinationUpdate(AstNode node) { 
		return; 
    }

	@Override
	public void destinationMove(AstNode node) { 
		return; 
	}

	@Override
	public void destinationInsert(AstNode node) { }
	
    
	@Override
	public void pre() { 
		ParserASTNode<AstNode> srcTree = this.context.dstTree.getASTNode();
		AstNode root = srcTree.getASTNode();
		IdentifierNodeVisitor visitor = new IdentifierNodeVisitor();
		root.visit(visitor);
		this.identifiersInDestination = visitor.identifiers;
	}
	
	@Override
	public void post() { 
		for(DNESpecs data : this.uses.values()) {
            this.registerAlert(new DoesNotExistAlert(this.getCheckerType(), data.srcIdentifier, data.dstIdentifier, data.type));
		}
	}

	@Override
	public String getCheckerType() {
		return TYPE;
	}
	
    /** 
     * Check if this node is a SimpleName that is part of a field.
     * If its parent was not deleted, add it to one of the sets.
     * 
     * The Name node must be the field in a field access that was not itself
     * deleted. 
     */
	private void checkSourceName(AstNode node) {

		if(node instanceof Name && 
		   node.getParent() instanceof InfixExpression && 
		   node == ((InfixExpression)node.getParent()).getRight() &&
		   CheckerUtilities.isIdentifierOperator(((InfixExpression)node.getParent()).getOperator()) &&
		   this.context.getSrcChangeOp(((InfixExpression)node.getParent()).getLeft()) != ChangeType.DELETE &&
		   this.context.getSrcChangeOp(((InfixExpression)node.getParent()).getLeft()) != ChangeType.UPDATE) {
			
			AstNode topLevelIdentifierNode = CheckerUtilities.getTopLevelIdentifier(node);

            /* Get the identifiers for the source and destination field accesses. */
            AstNode dstIdentifier = this.context.getDstNodeMapping(topLevelIdentifierNode);
            String dstString = "UNKNOWN";
            if(dstIdentifier != null) dstString = CheckerUtilities.getIdentifier(dstIdentifier);

            String srcString = CheckerUtilities.getIdentifier(topLevelIdentifierNode);
            
            if(srcString != null && dstString != null && dstString != "UNKNOWN") {
            	
            	/* Don't include it if the identifier is still present in the destination file. */
            	if(!this.identifiersInDestination.contains(srcString)) {
            		
                    NameType nameType = DoesNotExistCheckerUtilities.getNameType((Name)node);
                    DNESpecs data = new DNESpecs(srcString, dstString, nameType);
                    this.uses.put(srcString, data);

            	}
            }
		}
	}
	
	private class DNESpecs {
		
		public String srcIdentifier;
		public String dstIdentifier;
		public NameType type;
		
		public DNESpecs(String srcIdentifier, String dstIdentifier, NameType type) {
			this.srcIdentifier = srcIdentifier;
			this.dstIdentifier = dstIdentifier;
			this.type = type;
		}
		
	}
	
	private class IdentifierNodeVisitor implements NodeVisitor {
		
		public HashSet<String> identifiers;
		
		public IdentifierNodeVisitor() {
			this.identifiers = new HashSet<String>();
		}

		@Override
		public boolean visit(AstNode node) {
			
			if(node instanceof Name) {
				String identifier = CheckerUtilities.getIdentifier(CheckerUtilities.getTopLevelIdentifier(node));
				if(identifier != null) {
					this.identifiers.add(identifier);
				}
			}

			return true;
		}
		
	}
	
}
