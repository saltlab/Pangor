package ca.ubc.ece.salt.sdjsb.analysis.specialtype;

import java.util.HashSet;
import java.util.Set;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.ObjectProperty;

import ca.ubc.ece.salt.sdjsb.analysis.AnalysisUtilities;

/**
 * A visitor for finding identifier uses.
 * 
 * An identifier is used if it is passed as an argument, if one one of its
 * fields or methods are accessed or if it is dereferenced in an expression.
 * @author qhanam
 */
public class AssignmentTreeVisitor implements NodeVisitor {
    
    private Set<String> assignedIdentifiers;
    
    public AssignmentTreeVisitor() {
        this.assignedIdentifiers = new HashSet<String>();
    }
    
    /**
     * @return the list of identifiers that were used.
     */
    public Set<String> getAssignedIdentifiers() {
    	return this.assignedIdentifiers;
    }
    
    public boolean visit(AstNode node) {

        /* Investigate the subtrees. */
        if (node instanceof Assignment || node instanceof ObjectProperty) {

        	InfixExpression assignment = (InfixExpression) node;
            String identifier = AnalysisUtilities.getIdentifier(assignment.getLeft());
            if(identifier != null) this.assignedIdentifiers.add(identifier);

        } 

        return true;
    }

}
