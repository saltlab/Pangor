package ca.ubc.ece.salt.sdjsb.analysis.specialtype;

import java.util.LinkedList;
import java.util.List;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;

/**
 * A visitor for finding special type checks in conditions.
 * @author qhanam
 */
public class SpecialTypeVisitor implements NodeVisitor {
    
    private List<SpecialTypeCheck> specialTypeChecks;
    private AstNode condition;
    
    public SpecialTypeVisitor(AstNode condition) {
		this.specialTypeChecks = new LinkedList<SpecialTypeCheck>();
		this.condition = condition;
    }
    
    /**
     * @return the list of special type checks found by the visitor.
     */
    public List<SpecialTypeCheck> getSpecialTypeChecks() {
    	return this.specialTypeChecks;
    }
    
    /**
     * Visits each element of the condition and looks for special type checks.
     */
    public boolean visit(AstNode node) {
    	
    	/* We only inspect inserted conditions. */
    	if(node.getChangeType() == ChangeType.INSERTED) {

            SpecialTypeCheck stc = SpecialTypeAnalysisUtilities.getSpecialTypeCheck(condition, node);
            if(stc != null) this.specialTypeChecks.add(stc);

    	}

        return true;

    }

}
