package ca.ubc.ece.salt.sdjsb.checker.doesnotexist;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.sdjsb.checker.AbstractChecker;
import ca.ubc.ece.salt.sdjsb.checker.CheckerContext;
import ca.ubc.ece.salt.sdjsb.checker.CheckerContext.ChangeType;
import ca.ubc.ece.salt.sdjsb.checker.CheckerUtilities;
import ca.ubc.ece.salt.sdjsb.checker.doesnotexist.DoesNotExistCheckerUtilities.NameType;

/**
 * Detects repairs that fix variables, fields or functions that do not exist.
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
	 * Keeps track of assignments of variables and fields that have been
	 * modified.
	 */
	private Map<String, DNESpecs> assignments;
	
	/**
	 * Keeps track of comparisons of variables and fields that have been
	 * modified.
	 */
	private Set<String> comparisons;
	
	/**
	 * Keeps track of function calls that have been modified.
	 */
	private Set<String> functions;
	
	public DoesNotExistChecker(CheckerContext context) {
		super(context);
		this.assignments = new HashMap();
		this.comparisons = new HashSet();
		this.functions = new HashSet();
	}

	@Override
	public void sourceDelete(AstNode node) { 
		/* Check if this node is a SimpleName that is part of a field or a
		 * variable. If its parent was not deleted, add it to one of the
		 * Sets. */
		
		if(node instanceof Name) {
			
			AstNode topLevelIdentifierNode = CheckerUtilities.getTopLevelIdentifier(node);
			ChangeType parentChangeType = this.context.getSrcChangeOp(topLevelIdentifierNode.getParent());
			
			if(parentChangeType != ChangeType.DELETE) {
				
				
                /* TODO: This is a bit of a hack to see if it works, which it does!.
                 * 		 In some cases (i.e. for variables) the top level identifier will be deleted... so we
                 * 		 need to handle this somehow... */
                AstNode dstIdentifier = this.context.getDstNodeMapping(topLevelIdentifierNode);
                String dstString = "UNKNOWN";
                if(dstIdentifier != null) dstString = CheckerUtilities.getIdentifier(dstIdentifier);

				String srcString = CheckerUtilities.getIdentifier(topLevelIdentifierNode);
				NameType nameType = DoesNotExistCheckerUtilities.getNameType((Name)node);

				DNESpecs data = new DNESpecs(srcString, dstString, nameType);

				this.assignments.put(srcString, data);
			}
		}
	}

	@Override
	public void sourceUpdate(AstNode node) { return; }

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
	public void pre() { }
	
	@Override
	public void post() { 
		for(DNESpecs data : this.assignments.values()) {
            this.registerAlert(new DoesNotExistAlert(this.getCheckerType(), data.srcIdentifier, data.dstIdentifier, data.type));
		}
	}

	@Override
	public String getCheckerType() {
		return TYPE;
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
	
}
