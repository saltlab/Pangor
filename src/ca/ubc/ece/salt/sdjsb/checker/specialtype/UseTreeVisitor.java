package ca.ubc.ece.salt.sdjsb.checker.specialtype;

import java.util.Set;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.sdjsb.checker.CheckerContext;
import ca.ubc.ece.salt.sdjsb.checker.CheckerContext.ChangeType;
import ca.ubc.ece.salt.sdjsb.checker.CheckerUtilities;

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

            /* Investigate the subtrees. */
            if (node instanceof Assignment) {

                AstNode right = ((Assignment)node).getRight();
            	this.check(right);

            } 
            else if (node instanceof InfixExpression) {
                InfixExpression ie = (InfixExpression) node;
                
                /* If this is not a use operator, check that neither side
                 * is an identifier. */
                if(SpecialTypeCheckerUtilities.isUseOperator(ie.getOperator())) {
                    AstNode left = ie.getLeft();
                    this.check(left);

                    if(ie.getOperator() != Token.DOT 
                       && ie.getOperator() != Token.GETPROP 
                       && ie.getOperator() != Token.GETPROPNOWARN)
                    {
                        AstNode right = ie.getRight();
                        this.check(right);
                    }
                }
            } 
            else if (node instanceof FunctionCall) {

            	FunctionCall call = (FunctionCall) node;
            	for(AstNode argument : call.getArguments()) {
            		this.check(argument);
            	}
            	this.check(call.getTarget());

            }
            else if (node instanceof ConditionalExpression) {
            	
            	ConditionalExpression ce = (ConditionalExpression) node;
            	this.check(ce.getTrueExpression());
            	this.check(ce.getFalseExpression());
            	
            }
            
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
        ChangeType changeType = context.getDstChangeOp(node);
       
        if(changeType == ChangeType.INSERT || changeType == ChangeType.UPDATE) {
            String identifier = CheckerUtilities.getIdentifier(node);

            if(identifier != null && this.variableIdentifiers.contains(identifier)) {
                /* We have found an instance of a variable use. */
                this.variableIdentifiers.remove(identifier);
            }
        	
        }
    }
    
}
