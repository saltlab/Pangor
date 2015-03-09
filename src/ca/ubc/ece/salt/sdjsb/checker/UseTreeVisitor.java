package ca.ubc.ece.salt.sdjsb.checker;

import java.util.Set;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.sdjsb.checker.CheckerContext.ChangeType;

/**
 * A visitor for finding identifier uses.
 * 
 * An identifier is used if it is passed as an argument, if one one of its
 * fields or methods are accessed or if it is dereferenced in an expression.
 * @author qhanam
 */
public class UseTreeVisitor implements NodeVisitor {
    
    private CheckerContext context;
    private Set<String> variableIdentifiers;
    
    public UseTreeVisitor(CheckerContext context, Set<String> variableIdentifier) {
    	this.context = context;
        this.variableIdentifiers = variableIdentifier;
    }
    
    public boolean visit(AstNode node) {
        /* If this node has been inserted or updated, investigate its
         * children to see if a variableIdenfifier is used. */

        ChangeType changeType = context.getDstChangeOp(node);
        
        if(changeType == ChangeType.INSERT || changeType == ChangeType.UPDATE) {
            /* Check if this node is an identifier. */
            check(node);

            /* Investigate the subtrees. */
            if (node instanceof Assignment) {
                visit(((Assignment)node).getRight());
                return false;
            } else if (node instanceof InfixExpression) {
                InfixExpression ie = (InfixExpression) node;
                
                /* If this is not a use operator, check that neither side
                 * is an identifier. */
                if(!Utilities.isUseOperator(ie.getOperator())) {

                    String left = Utilities.getIdentifier(ie.getLeft());
                    String right = Utilities.getIdentifier(ie.getRight());
                    if(left == null || !this.variableIdentifiers.contains(left)) visit(ie.getLeft());
                    if(right == null || !this.variableIdentifiers.contains(right)) visit(ie.getRight());
                    
                    return false;
                }
                else {
                    /* FIXME: For some reason return true doens't work here. */
                    visit(ie.getLeft());
                    visit(ie.getRight());
                }
            }
            
            /* Anything else check the subtree. */
            return true;
        }

        return true;
    }

    /**
     * Checks if the AstNode is an identifier that is in the list of
     * identifiers that were checked in the parent. If they match,
     * the identifier has been used, so remove it from the list of
     * checked identifiers.
     * @param node
     */
    public void check(AstNode node) {
        String identifier = Utilities.getIdentifier(node);

        if(identifier != null && this.variableIdentifiers.contains(identifier)) {
            /* We have found an instance of a variable use. */
            this.variableIdentifiers.remove(identifier);
        }
    }
    
}
