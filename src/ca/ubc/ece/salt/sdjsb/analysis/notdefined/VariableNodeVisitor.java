package ca.ubc.ece.salt.sdjsb.analysis.notdefined;

import java.util.HashSet;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

/**
 * Gets all the variables that are used in the tree.
 */
public class VariableNodeVisitor implements NodeVisitor {
    
    private HashSet<String> identifiers;
    
    /**
     * Gets the set of variables that are used in the tree and that have a
     * change classification of unchanged or moved.
     * @param node The statement to inspect for variable uses.
     * @return The set of all variables that are used in the tree.
     */
    public static Set<String> getUsedVariables(AstNode node) {
    	VariableNodeVisitor visitor = new VariableNodeVisitor();
    	node.visit(visitor);
    	return visitor.identifiers;
    }
    
    public VariableNodeVisitor() {
        this.identifiers = new HashSet<String>();
    }

    @Override
    public boolean visit(AstNode node) {
        
        if(node instanceof Name && NotDefinedAnalysisUtilities.isVariable((Name)node)) {
        
            if(!(node.getParent() instanceof VariableInitializer) && (node.getChangeType() == ChangeType.MOVED || node.getChangeType() == ChangeType.UNCHANGED) ) {
                this.identifiers.add(((Name)node).getIdentifier());
            }
            
        }

        return true;
    }
    
}
