package ca.ubc.ece.salt.sdjsb.checker;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.sdjsb.checker.CheckerContext.ChangeType;
import ca.ubc.ece.salt.sdjsb.checker.alert.Alert;
import fr.labri.gumtree.actions.TreeClassifier;
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
	 * @param srcTreeNodeMap A map of AstNodes to Tree nodes in the source
	 * 		  file. 
	 * @param dstTreeNodeMap A map of AstNodes to Tree nodes in the destination
	 * 		  file. 
	 * @param threeClassifier The GumTree structure that provides access to the
	 * 		  Tree node classifications (inserted, deleted, updated, modified).
	 */
	public CheckerRegistry(Map<AstNode, Tree> srcTreeNodeMap, Map<AstNode, Tree> dstTreeNodeMap, TreeClassifier treeClassifier) {

		this.checkerContext = new CheckerContext(srcTreeNodeMap, dstTreeNodeMap, treeClassifier);
		
		this.activeCheckers = new LinkedList<AbstractChecker>();

		/* Create and add the default checkers. */
		this.activeCheckers.add(new SpecialTypeHandlingChecker(this.checkerContext));
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
	public void sourceDelete(AstNode node) { 
		for (AbstractChecker checker : activeCheckers) {
			checker.sourceDelete(node);
		}
	}

	/**
	 * Notifies the checkers of a source update event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	public void sourceUpdate(AstNode node) { 
		for (AbstractChecker checker : activeCheckers) {
			checker.sourceUpdate(node);
		}
    }
	
	/**
	 * Notifies the checers of a source move event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	public void sourceMove(AstNode node) { 
		for (AbstractChecker checker : activeCheckers) {
			checker.sourceMove(node);
		}
    }

	/**
	 * Notifies the checkers of a destination update event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	public void destinationUpdate(AstNode node) { 
		for (AbstractChecker checker : activeCheckers) {
			checker.destinationUpdate(node);
		}
    }
	
	/**
	 * Notifies the checkers of a destination move event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	public void destinationMove(AstNode node) { 
		for (AbstractChecker checker : activeCheckers) {
			checker.destinationMove(node);
		}
	}
	
	/**
	 * Notifies the checkers of a destination insert event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	public void destinationInsert(AstNode node) { 
		DestinationInsertVisitor div = new DestinationInsertVisitor();
		node.visit(div);
	}
	
	/**
	 * Notifies the checkers that the source and detination iterations have
	 * finished.
	 */
	public void finished() {
		for (AbstractChecker checker : activeCheckers) {
			checker.finished();
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
