package ca.ubc.ece.salt.sdjsb.ast;

import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.NodeVisitor;

public class ConditionalPreProcessor implements PreProcessor {

	@Override
	public void process(AstRoot root) {
		// TODO Auto-generated method stub

	}
	
	private class ConditionalNodeVisitor implements NodeVisitor {
		
//		private List<VariableInitializer> transforms;

		@Override
		public boolean visit(AstNode node) {
//			if(node instanceof ConditionalExpression) {
//				ConditionalExpression ce = (ConditionalExpression) node;
//				
//				if(ce.getParent() instanceof Assignment && ce.getParent().getScope()) {
//					
//				}
//				
//				
//			}
			return false;
		}
		
	}

}
