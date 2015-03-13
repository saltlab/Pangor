package ca.ubc.ece.salt.sdjsb.checker;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.sdjsb.checker.CheckerContext.ChangeType;
import ca.ubc.ece.salt.sdjsb.checker.specialtype.SpecialTypeChecker;
import fr.labri.gumtree.io.ParserASTNode;
import fr.labri.gumtree.tree.Tree;

/**
 * All implemented checkers should be registered here. When a modified Tree node
 * is reached in the iteration, the CheckerRegistry is notified, which then
 * notifies all the registered checkers.
 * 
 * @author qhanam
 */
public class CheckerRegistry {
	
	/**
	 * The list of checkers that should be initialized and receive events from
	 * the event bus.
	 */
	private List<AbstractChecker> activeCheckers;
	
	/**
	 * Provides checkers with access to GumTree information.
	 */
	private CheckerContext checkerContext;

	/**
	 * Creates and registers all the default checkers.
	 * 
	 * @param context The source and destination AST nodes, mappings and change
	 * 				  classifications. Provides context for the checkers to run
	 * 				  their analysis.
	 */
	public CheckerRegistry(CheckerContext context) {
		
		this.checkerContext = context;
		
		this.activeCheckers = new LinkedList<AbstractChecker>();

		/* Create and add the default checkers. */
		this.activeCheckers.add(new SpecialTypeChecker(this.checkerContext));
	}
	
	/**
	 * Runs the analysis by iterating through the source and destination tree
	 * pre-order and raising events that are handled by individual checkers.
	 */
	public void analyze() { 
		
		/* Trigger pre-processing events. */
		this.pre();

		/* Iterate the source tree. Call the CheckerRegistry to trigger events. */
		for (Tree t: this.checkerContext.srcTree.getTrees()) {
			if (this.checkerContext.treeClassifier.getSrcMvTrees().contains(t)) {
				ParserASTNode<AstNode> parserNode = t.getASTNode(); // TODO: We should pass the source AND destination nodes.
				this.sourceMove(parserNode.getASTNode());
			} if (this.checkerContext.treeClassifier.getSrcUpdTrees().contains(t)) {
				ParserASTNode<AstNode> parserNode = t.getASTNode();
				this.sourceUpdate(parserNode.getASTNode());
			} if (this.checkerContext.treeClassifier.getSrcDelTrees().contains(t)) {
				ParserASTNode<AstNode> parserNode = t.getASTNode();
				this.sourceDelete(parserNode.getASTNode());
			}
		}

		/* Iterate the destination tree. Call the CheckerRegistry to trigger events. */
		for (Tree t: this.checkerContext.dstTree.getTrees()) {
			if (this.checkerContext.treeClassifier.getDstMvTrees().contains(t)) {
				ParserASTNode<AstNode> parserNode = t.getASTNode();
				this.destinationMove(parserNode.getASTNode());
			} if (this.checkerContext.treeClassifier.getDstUpdTrees().contains(t)) {
				ParserASTNode<AstNode> parserNode = t.getASTNode();
				this.destinationUpdate(parserNode.getASTNode());
			} if (this.checkerContext.treeClassifier.getDstAddTrees().contains(t)) {
				ParserASTNode<AstNode> parserNode = t.getASTNode();
				this.destinationInsert(parserNode.getASTNode());
			}
		}
		
		/* Trigger post-processing events. */
		this.post();
	}
	
	/**
	 * Returns a list of the alerts from all checkers.
	 * @return The aggregated alerts from all checkers.
	 */
	public List<Alert> getAlerts() {
		List<Alert> alerts = new LinkedList<Alert>();
		for (AbstractChecker checker : activeCheckers) {
			alerts.addAll(checker.getAlerts());
		}
		return alerts;
	}
	
	/**
	 * Register a third party checker.
	 * @param checker
	 */
	public void register(AbstractChecker checker) {
		this.activeCheckers.add(checker);
	}
	
	/**
	 * Notifies the checkers of a source delete event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	private void sourceDelete(AstNode node) { 
		for (AbstractChecker checker : activeCheckers) {
			checker.sourceDelete(node);
		}
	}

	/**
	 * Notifies the checkers of a source update event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	private void sourceUpdate(AstNode node) { 
		for (AbstractChecker checker : activeCheckers) {
			checker.sourceUpdate(node);
		}
    }
	
	/**
	 * Notifies the checers of a source move event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	private void sourceMove(AstNode node) { 
		for (AbstractChecker checker : activeCheckers) {
			checker.sourceMove(node);
		}
    }

	/**
	 * Notifies the checkers of a destination update event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	private void destinationUpdate(AstNode node) { 
		for (AbstractChecker checker : activeCheckers) {
			checker.destinationUpdate(node);
		}
    }
	
	/**
	 * Notifies the checkers of a destination move event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	private void destinationMove(AstNode node) { 
		for (AbstractChecker checker : activeCheckers) {
			checker.destinationMove(node);
		}
	}
	
	/**
	 * Notifies the checkers of a destination insert event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	private void destinationInsert(AstNode node) { 
		DestinationInsertVisitor div = new DestinationInsertVisitor();
		node.visit(div);
	}

	/**
	 * Called before the modification operation events begin.
	 */
	private void pre() {
		/* Perform preprocessing operations on the Tree node maps (i.e. fix 
		 * mistakes). */
		PreProcessor preProcessor = new PreProcessor(this.checkerContext);
		preProcessor.process();
		for (AbstractChecker checker : activeCheckers) {
			checker.pre();
		}
	}
	
	/**
	 * Called after the modification operation events are complete.
	 */
	private void post() {
		for (AbstractChecker checker : activeCheckers) {
			checker.post();
		}
	}
	
	/**
	 * Walks the tree of the inserted node and triggers the destinationInsert
	 * event for each child that was inserted.
	 */
	private class DestinationInsertVisitor implements NodeVisitor {
		
		private CheckerContext context;
		private List<AbstractChecker> activeCheckers;
		
		public DestinationInsertVisitor() {
			this.context = CheckerRegistry.this.checkerContext;
			this.activeCheckers = CheckerRegistry.this.activeCheckers;
		}
		
		public boolean visit(AstNode node) {
			if(this.context.getDstChangeFlag(node) == ChangeType.MOVE) return false;
			
            for (AbstractChecker checker : activeCheckers) {
                checker.destinationInsert(node);
            }
                
			return true;
		}
		
	}
	
}
