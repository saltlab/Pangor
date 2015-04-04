package ca.ubc.ece.salt.sdjsb.checker;

import java.util.List;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.gumtree.ast.ParserASTNode;
import fr.labri.gumtree.tree.Tree;

public class PreProcessor {
	
	private CheckerContext context;
	
	public PreProcessor(CheckerContext context) {
		this.context = context;
	}
	
	public void process() {
		this.processConditionalStatements();
	}
	
	/**
	 * Fixes matching errors in conditional statement insertions.
	 * 
	 * These are statements of the form: [variable|field] [=|:] [condition] ? [true path] : [false path].
	 * 
	 * GumTree fails to match the right hand side of a variable declaration to 
	 * the true path of the conditional statement. This method fixes that by
	 * adding a MOVE operation if the RHS of the old assignment is the same as
	 * the TRUE path of the new conditional statement.
	 * 
	 * TODO: Make PreProcessor an abstract class and move everything to its own package.
	 */
	private void processConditionalStatements() {
		
		/* Iterate through the destination Tree nodes. */
		for (Tree t: this.context.dstTree.getTrees()) {
			
			/* Look for Tree nodes that were inserted. */
			if (this.context.treeClassifier.getDstAddTrees().contains(t)) {
				
				AstNode node = t.<AstNode>getASTNode();
				
				/* Check if the inserted node is a conditional statement. */
				if(node instanceof ConditionalExpression) {

					ConditionalExpression conditionalExpression = (ConditionalExpression) node;

					/* Get the tree parent. */
					Tree dstParentTree = t.getParent();
					AstNode dstParentNode = (AstNode)dstParentTree.getASTNode();
					
					/* Find the source Tree node that maps to the destination
					 * Tree node. */
					Tree srcParentTree = this.context.mappings.getSrc(dstParentTree);
					AstNode srcParentNode = (AstNode)srcParentTree.getASTNode();
					
					if(srcParentTree != null && dstParentNode == node.getParent()) {

                        AstNode srcRHS = null;
                        AstNode dstTrue = conditionalExpression.getTrueExpression();
                        AstNode dstFalse = conditionalExpression.getFalseExpression();

                        /* The parent should be some sort of assignment node. */
                        if(srcParentNode instanceof VariableInitializer) {
                            VariableInitializer srcAssignment = (VariableInitializer) srcParentNode;
                            srcRHS = srcAssignment.getInitializer();
                        }
                        else if(srcParentNode instanceof ObjectProperty || srcParentNode instanceof Assignment) {
                            InfixExpression srcAssignment = (InfixExpression) srcParentNode;
                            srcRHS = srcAssignment.getRight();
                        }
                        
                        /* If the RHS identifiers match, add the node to the 'MOVE' list. */
                        if(srcRHS != null && dstTrue != null) {
                            List<String> srcIdentifiers = CheckerUtilities.getRHSIdentifiers(srcRHS);
                            
                            if(dstTrue != null) {
                                String dstIdentifier = CheckerUtilities.getIdentifier(dstTrue);

                                for(String srcIdentifier : srcIdentifiers) {
                                    if(srcIdentifier != null && dstIdentifier != null && srcIdentifier.equals(dstIdentifier)) {
                                        Tree dstRHSTree = this.context.dstTreeNodeMap.get(dstTrue);
                                        this.context.treeClassifier.getDstMvTrees().add(dstRHSTree);
                                    }
                                }
                            }
                            if(dstFalse != null) {
                                String dstIdentifier = CheckerUtilities.getIdentifier(dstFalse);

                                for(String srcIdentifier : srcIdentifiers) {
                                    if(srcIdentifier != null && dstIdentifier != null && srcIdentifier.equals(dstIdentifier)) {
                                        Tree dstRHSTree = this.context.dstTreeNodeMap.get(dstTrue);
                                        this.context.treeClassifier.getDstMvTrees().add(dstRHSTree);
                                    }
                                }
                            }
                        }
						
					}
					
				}
			} 
		}
	}
}
