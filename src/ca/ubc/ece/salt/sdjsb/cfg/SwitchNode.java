package ca.ubc.ece.salt.sdjsb.cfg;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.EmptyStatement;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.StringLiteral;

/**
 * A CFGNode that has multiple exit edges depending on the value of the switch
 * variable. There is also a default exit edge if no cases match. 
 * @author qhanam
 */
public class SwitchNode extends CFGNode {
	
	private Map<AstNode, CFGNode> caseMap;
	public CFGNode mergeNode;
	
	public SwitchNode(AstNode statement) {
		super(statement);
		this.caseMap = new LinkedHashMap<AstNode, CFGNode>();
	}
	
	public void setCase(AstNode expression, CFGNode then) {
		this.caseMap.put(expression, then);
	}

	public Set<AstNode> getCases() {
		return this.caseMap.keySet();
	}
	
	public CFGNode getCase(AstNode literal) {
		return this.caseMap.get(literal);
	}

	@Override
	public void mergeInto(CFGNode nextNode) {
		
		/* Assign the default case to be the next node. */
		this.mergeNode = nextNode;
		
	}

	@Override
	public String printSubGraph(CFGNode mergeNode) {

        String s = "CASE{";
        
        Set<AstNode> cases = this.getCases();
        AstNode current = new EmptyStatement();;

        for(AstNode next : cases) {
        	
        	if(!(current instanceof EmptyStatement)) {
        		
                if(current instanceof NumberLiteral) {
                    s += ((NumberLiteral)current).getValue() + ":";
                }
                else if(current instanceof StringLiteral) {
                    s += ((StringLiteral)current).getValue() + ":";
                }
                else {
                	s += "default:";
                }

                CFGNode caseNode = this.getCase(current);
                
                /* The merge node is the node that follows this node in the
                 * switch statement. Either the printer will hit a break
                 * statement and return, or it will hit the next case in the
                 * switch, which is the merge node. */
                s += caseNode.printSubGraph(this.getCase(next)) + "->";
        		
        	}
        	
        	current = next;
        	
        }

        if(current instanceof NumberLiteral) {
            s += ((NumberLiteral)current).getValue() + ":";
        }
        else if(current instanceof StringLiteral) {
            s += ((StringLiteral)current).getValue() + ":";
        }
        else {
            s += "default:";
        }

        CFGNode caseNode = this.getCase(current);
        s += caseNode.printSubGraph(this.mergeNode) + "}";

        if(mergeNode == this.mergeNode) {
            /* We are not at the bottom level of the merge. */
            return s;
        } 

        /* We are at the bottom level of the merge. */
        String subGraph = this.mergeNode.printSubGraph(mergeNode);
        if(subGraph.charAt(0) == '}') {
            return s + subGraph;
        }
        return s + "->" + subGraph;

	}

}
