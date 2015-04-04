package ca.ubc.ece.salt.sdjsb.checker.notdefined;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.gumtree.ast.ParserASTNode;
import ca.ubc.ece.salt.sdjsb.checker.AbstractChecker;
import ca.ubc.ece.salt.sdjsb.checker.CheckerContext;

/**
 * Detects repairs that fix undefined variables.
 * 
 * For example, if a repair declares a variable (i.e. "var [variable]") that is
 * used in the source file but is undeclared in the source file.
 * 
 * @author qhanam
 */
public class NotDefinedChecker extends AbstractChecker {
	
	private static final String TYPE = "ND";
	
	/**
	 * Keeps track of deleted variable declarations.
	 */
	private Set<String> deleted;
	
	/**
	 * Keeps track of inserted variable declarations.
	 */
	private Set<String> inserted;

	/**
	 * Keeps track of inserted variable declarations.
	 */
	private Set<String> used;

	/**
	 * The set of used variables in the source file.
	 */
	private Set<String> variablesUsedInSource;

	public NotDefinedChecker(CheckerContext context) {
		super(context);
		this.deleted = new HashSet<String>();
		this.inserted = new HashSet<String>();
		this.used = new HashSet<String>();
	}

	@Override
	public void sourceDelete(AstNode node) { }

	@Override
	public void sourceUpdate(AstNode node) { }

	@Override
	public void sourceMove(AstNode node) { }

	@Override
	public void destinationUpdate(AstNode node) { }

	@Override
	public void destinationMove(AstNode node) { }

	@Override
	public void destinationInsert(AstNode node) { 
		
		/* Add inserted variable declarations to the inserted set. */
		
		if(node instanceof VariableDeclaration) { 
			VariableDeclaration vd = (VariableDeclaration) node;
			List<VariableInitializer> variables = vd.getVariables();
			for(VariableInitializer variable : variables) {
				this.addInsertedVariable(variable);
			}
		}
		else if(node instanceof VariableInitializer) { 
			VariableInitializer variable = (VariableInitializer) node;
            this.addInsertedVariable(variable);
		}
		
		/* Add variable uses to the use set. */
		
		else if(node instanceof Name) {
			if(!(node.getParent() instanceof VariableInitializer)) {
				
				/* Check if this is a variable. It is a variable if it is the
				 * leftmost node in a field access. */
				Name name = (Name) node;
				if(NotDefinedCheckerUtilities.isVariable(name)) {
					this.used.add(name.getIdentifier());
				}
			}
		}
		
	}

	/**
	 * Adds a variable identifier from the variable initializer to the set of
	 * inserted variables.
	 * @param vi The variable initializer.
	 */
	private void addInsertedVariable(VariableInitializer vi) {
		AstNode target = vi.getTarget();
		if(target instanceof Name) {
			Name name = (Name) target;
            this.inserted.add(name.getIdentifier());
		}
	}

	/**
	 * Adds a variable identifier from the variable initializer to the set of
	 * deleted variables.
	 * @param vi The variable initializer.
	 */
	private void addDeletedVariable(VariableInitializer vi) {
		AstNode target = vi.getTarget();
		if(target instanceof Name) {
			Name name = (Name) target;
            this.deleted.add(name.getIdentifier());
		}
	}
    
	@Override
	public void pre() { 
		ParserASTNode<AstNode> srcTree = this.context.srcTree.getASTNode();
		AstNode root = srcTree.getASTNode();
		VariableNodeVisitor visitor = new VariableNodeVisitor();
		root.visit(visitor);
		this.variablesUsedInSource = visitor.identifiers;
	}
	
	@Override
	public void post() { 

        /* An undeclared variable was fixed if it was:
         * 	1. Declared (inserted) in the destination file.
         * 	2. Not declared (deleted) in the source file.
         * 	3. Not used (inserted) in the destination file.
         * 	4. Used in the source file. */
		for(String variable : this.inserted) {
			
			if(!this.deleted.contains(variable) 
					&& !this.used.contains(variable) 
					&& this.variablesUsedInSource.contains(variable)) {
                this.registerAlert(new NotDefinedAlert(this.getCheckerType(), variable));
			}

		}
	}

	@Override
	public String getCheckerType() {
		return TYPE;
	}

	/**
	 * Gets all the variables that are used in the tree.
	 */
	private class VariableNodeVisitor implements NodeVisitor {
		
		public HashSet<String> identifiers;
		
		public VariableNodeVisitor() {
			this.identifiers = new HashSet<String>();
		}

		@Override
		public boolean visit(AstNode node) {
			
			if(node instanceof Name && NotDefinedCheckerUtilities.isVariable((Name)node)) {
			
				if(!(node.getParent() instanceof VariableInitializer)) {

                    this.identifiers.add(((Name)node).getIdentifier());
					
				}
				
				else {
					
					/* To reduce false positives, add any variables initalized
					 * in the source file to the deleted list. */
                    VariableInitializer variable = (VariableInitializer) node.getParent();
                    NotDefinedChecker.this.addDeletedVariable(variable);

				}
				
			}

			return true;
		}
		
	}
	
}
