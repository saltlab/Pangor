package ca.ubc.ece.salt.sdjsb.checker;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;

/**
 * All implemented checkers should be registered here. When a modified Tree node
 * is reached in the iteration, the CheckerRegistry is notified, which then
 * notifies all the registered checkers.
 * 
 * @author qhanam
 */
public class CheckerRegistry {
	
	List<AbstractChecker> activeCheckers;

	/**
	 * Creates and registers all the default checkers.
	 */
	public CheckerRegistry() {
		this.activeCheckers = new LinkedList<AbstractChecker>();

		/* Create and add the default checkers. */
		this.activeCheckers.add(new SpecialTypeHandlingChecker());
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
		for (AbstractChecker checker : activeCheckers) {
			checker.destinationInsert(node);
		}
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
	
}
