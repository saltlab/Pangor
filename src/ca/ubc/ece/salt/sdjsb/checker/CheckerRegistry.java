package ca.ubc.ece.salt.sdjsb.checker;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
 * CheckerRegistry also raises events for subtree nodes. Since GumTree only 
 * classifies the parent tree, we visit the subtree and give unclassified 
 * nodes the same class as the parent.
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

	}

	/**
	 * Registers a checker to run. Be careful, a lot could go wrong here.
	 * @param checker The checker class to instantiate.
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public void register(String checker) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IllegalArgumentException, ClassNotFoundException {
		Class<?> c = Class.forName(checker);
		Constructor<?> constructor = c.getConstructor(CheckerContext.class);
		AbstractChecker concreteChecker = (AbstractChecker) constructor.newInstance(this.checkerContext);
		this.activeCheckers.add(concreteChecker);
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
	 * Notifies the checkers of a source delete event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	private void sourceDelete(AstNode node) { 
		CheckerEventNotifier notifier = new CheckerEventNotifier(this.activeCheckers) {
			@Override
			public void notify(AbstractChecker checker, AstNode node) {
                checker.sourceDelete(node);
			}
		};
		NodeChangeVisitor div = new NodeChangeVisitor(notifier, ChangeType.DELETE);
		node.visit(div);
	}

	/**
	 * Notifies the checkers of a source update event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	private void sourceUpdate(AstNode node) { 
		CheckerEventNotifier notifier = new CheckerEventNotifier(this.activeCheckers) {
			@Override
			public void notify(AbstractChecker checker, AstNode node) {
                checker.sourceUpdate(node);
			}
		};
		NodeChangeVisitor div = new NodeChangeVisitor(notifier, ChangeType.UPDATE);
		node.visit(div);
    }
	
	/**
	 * Notifies the checers of a source move event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	private void sourceMove(AstNode node) { 
		CheckerEventNotifier notifier = new CheckerEventNotifier(this.activeCheckers) {
			@Override
			public void notify(AbstractChecker checker, AstNode node) {
                checker.sourceMove(node);
			}
		};
		NodeChangeVisitor div = new NodeChangeVisitor(notifier, ChangeType.MOVE);
		node.visit(div);
    }

	/**
	 * Notifies the checkers of a destination update event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	private void destinationUpdate(AstNode node) { 
		CheckerEventNotifier notifier = new CheckerEventNotifier(this.activeCheckers) {
			@Override
			public void notify(AbstractChecker checker, AstNode node) {
                checker.destinationUpdate(node);
			}
		};
		NodeChangeVisitor div = new NodeChangeVisitor(notifier, ChangeType.UPDATE);
		node.visit(div);
    }
	
	/**
	 * Notifies the checkers of a destination move event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	private void destinationMove(AstNode node) { 
		CheckerEventNotifier notifier = new CheckerEventNotifier(this.activeCheckers) {
			@Override
			public void notify(AbstractChecker checker, AstNode node) {
                checker.destinationMove(node);
			}
		};
		NodeChangeVisitor div = new NodeChangeVisitor(notifier, ChangeType.MOVE);
		node.visit(div);
	}
	
	/**
	 * Notifies the checkers of a destination insert event.
	 * @param node The Rhino AstNode that was deleted.
	 */
	private void destinationInsert(AstNode node) { 
		CheckerEventNotifier notifier = new CheckerEventNotifier(this.activeCheckers) {
			@Override
			public void notify(AbstractChecker checker, AstNode node) {
                checker.destinationInsert(node);
			}
		};
		NodeChangeVisitor div = new NodeChangeVisitor(notifier, ChangeType.INSERT);
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
	private class NodeChangeVisitor implements NodeVisitor {
		
		private CheckerContext context;
		private CheckerEventNotifier checkerEventNotifier;
		private ChangeType changeType;
		
		public NodeChangeVisitor(CheckerEventNotifier checkerEventNotifier, ChangeType changeType) {
			this.context = CheckerRegistry.this.checkerContext;
			this.checkerEventNotifier = checkerEventNotifier;
			this.changeType = changeType;
		}
		
		public boolean visit(AstNode node) {
			ChangeType changeType;
			
			/* If this is a delete operation, we need to look in the source
			 * change list because deletes are not present in the destination
			 * change list. */
			if(this.changeType == ChangeType.DELETE) changeType = this.context.getSrcChangeFlag(node);
			else changeType = this.context.getDstChangeFlag(node);

			if(changeType != this.changeType && changeType != ChangeType.UNCHANGED) {
				return false;
			}
            this.checkerEventNotifier.notifyAll(node);
			return true;
		}
		
	}
	
	/**
	 * Notifies the subscribed checkers of an event. An event could be the
	 * insertion, deletion, update of moving of an ASTNode.
	 */
	private abstract class CheckerEventNotifier {
		
		private List<AbstractChecker> subscribers;
		
		public CheckerEventNotifier(List<AbstractChecker> subscribers) {
			this.subscribers = subscribers;
		}

		/**
		 * Notifies all subscribers that an event has occurred.
		 * @param node The node that was changed.
		 */
		public void notifyAll(AstNode node) {
            for (AbstractChecker checker : subscribers) {
                this.notify(checker, node);
            }
		}
		
		/**
		 * Notifies a subscriber that an event has occurred.
		 * @param node The node that was changed.
		 */
		public abstract void notify(AbstractChecker checker, AstNode node);
		
	}
	
}
