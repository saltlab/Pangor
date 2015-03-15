package ca.ubc.ece.salt.sdjsb.checker.specialtype;

import java.util.Set;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.VariableInitializer;

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

        /* Investigate the subtrees. */
        if (node instanceof Assignment || node instanceof ObjectProperty) {

            AstNode right = ((InfixExpression)node).getRight();
            this.check(right);

        } 
        else if (node instanceof VariableInitializer) {
        	
        	AstNode right = ((VariableInitializer)node).getInitializer();
        	this.check(right);
        	
        }
        else if (node instanceof ElementGet) {
        	
        	AstNode element = ((ElementGet)node).getElement();
        	this.check(element);
        	
        }
        else if (node instanceof InfixExpression) {
            InfixExpression ie = (InfixExpression) node;
            
            /* Only check if it is a use operator (for a field or function dereference). */
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
        else if (node instanceof ArrayLiteral) {
        	ArrayLiteral literal = (ArrayLiteral) node;
        	for(AstNode element : literal.getElements()) {
                this.check(element);
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
       
        if(changeType == ChangeType.MOVE || changeType == ChangeType.UNCHANGED) {
            String identifier = CheckerUtilities.getIdentifier(node);

            if(identifier != null && this.variableIdentifiers.contains(identifier)) {
                /* We have found an instance of a variable use. */
                this.variableIdentifiers.remove(identifier);
            }
        	
        }
    }
    
}
