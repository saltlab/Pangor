package ca.ubc.ece.salt.sdjsb.analysis.specialtype;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
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
    
    private List<Pair<String, AstNode>> assignedIdentifiers;
    
    public AssignmentTreeVisitor() {
        this.assignedIdentifiers = new LinkedList<Pair<String, AstNode>>();
    }
    
    /**
     * @return the list of identifiers that were used.
     */
    public List<Pair<String, AstNode>> getAssignedIdentifiers() {
    	return this.assignedIdentifiers;
    }
    
    public boolean visit(AstNode node) {

        /* Investigate the subtrees. */
        if (node instanceof Assignment || node instanceof ObjectProperty) {

        	InfixExpression assignment = (InfixExpression) node;
            String identifier = AnalysisUtilities.getIdentifier(assignment.getLeft());
            if(identifier != null) this.assignedIdentifiers.add(Pair.of(identifier, assignment.getRight()));
            
            

        } 

        return true;
    }

}
