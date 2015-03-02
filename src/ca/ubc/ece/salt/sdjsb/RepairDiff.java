package ca.ubc.ece.salt.sdjsb;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import fr.labri.gumtree.actions.RootAndLeavesClassifier;
import fr.labri.gumtree.actions.TreeClassifier;
import fr.labri.gumtree.algo.StringAlgorithms;
import fr.labri.gumtree.client.DiffClient;
import fr.labri.gumtree.client.DiffOptions;
import fr.labri.gumtree.client.TreeGeneratorRegistry;
import fr.labri.gumtree.io.ParserASTNode;
import fr.labri.gumtree.matchers.MappingStore;
import fr.labri.gumtree.matchers.Matcher;
import fr.labri.gumtree.matchers.MatcherFactories;
import fr.labri.gumtree.tree.Tree;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;

import ca.ubc.ece.salt.sdjsb.SpecialTypeMap.SpecialType;

public class RepairDiff extends DiffClient {
	
	private SpecialTypeMap assignments;
	private SpecialTypeMap comparisons;

	public RepairDiff(DiffOptions diffOptions) {
		super(diffOptions);
		this.assignments = new SpecialTypeMap();
		this.comparisons = new SpecialTypeMap();
	}

	@Override
	public void start() {
		/* Get the files we are comparing from the command line arguments. */
		File fSrc = new File(diffOptions.getSrc());
		File fDst = new File(diffOptions.getDst());

        /* Create the abstract GumTree representations of the ASTs. */
        Tree src;
        Tree dst;
        try{
            src = TreeGeneratorRegistry.getInstance().getTree(fSrc.getAbsolutePath());
            dst = TreeGeneratorRegistry.getInstance().getTree(fDst.getAbsolutePath());
        } catch (IOException e) {
        	System.err.println(e.getMessage());
        	return;
        }
		
		/* Match the source AST nodes to the destination AST nodes. The default
		 * algorithm for doing this is the GumTree algorithm. */
		Matcher matcher = MatcherFactories.newMatcher(src, dst);
		matcher.match();
		
		/* Produce the diff object that we will use to infer properties of
		 * repairs. */
		try{
            this.produce(src, dst, matcher);
		} catch (IOException e) {
        	System.err.println(e.getMessage());
        	return;
		}
	}
	
	/**
	 * Computes the set of changes that transforms the source AST into the
	 * destination AST. Each change is one of {delete, add, move, update}.
	 * @param src The GumTree AST generated for the source file.
	 * @param dst The GumTree AST generated for the destination file.
	 * @param matcher The set of source nodes matched to destination nodes.
	 * @throws IOException
	 */
	private void produce(Tree src, Tree dst, Matcher matcher) throws IOException {
		
		/* Classify parts of each tree as deleted, added, moved or updated. The
		 * source tree nodes can be deleted or updated, while the destination
		 * tree nodes can be added, moved or updated. Moved and deleted nodes
		 * are mapped from the source tree to the destination tree. 
		 * 
		 * The classified nodes are stored in hash maps:
		 *  getSrcDeleteTrees() - gets the map containing all delete ops.
		 * 	get[Src|Dst]MvTrees() - gets the map containing all move operations.
		 *  get[Src|Dst]UpdateTrees() - gets the map containing all update ops.
		 *  getDstAddTrees() - gets the map containing all */
		TreeClassifier c = new RootAndLeavesClassifier(src, dst, matcher);

		/* We use mapping ids to keep track of mapping changes from the source
		 * to the destination. */
		MappingStore mappings = matcher.getMappings();
		
		
		/* Iterate the source tree. */
		for (Tree t: src.getTrees()) {
			if (c.getSrcMvTrees().contains(t)) {
				/* This Tree node is moved. */
			} if (c.getSrcUpdTrees().contains(t)) {
				/* This Tree node is updated. */

				/* Separate the updated statement into changed and unchanged
				 * parts by doing using string matching. */
				List<int[]> hunks = StringAlgorithms.hunks(t.getLabel(), mappings.getDst(t).getLabel());
				
				/* Iterate through the parts of the node that were updated. */
				for(int[] hunk: hunks) {
					/* This hunk was updated. */
				}
			} if (c.getSrcDelTrees().contains(t)) {
				/* This Tree node is deleted. */
			}
		}

		/* Iterate the desgination tree. 
		 * 
		 * We need to keep track of:
		 *  1. Statements that insert conditionals.
		 *  2. Statements that insert assignments. */
		for (Tree t: dst.getTrees()) {
			if (c.getDstMvTrees().contains(t)) {
				/* This Tree node is moved. */
			} if (c.getDstUpdTrees().contains(t)) {
				/* This tree node is updated. */
			} if (c.getDstAddTrees().contains(t)) {
				/* This tree node is added. */
				
				/* Get the Rhino AstNode. */
				ParserASTNode<AstNode> parserNode = t.getASTNode();
				AstNode node = parserNode.getASTNode(); 
				System.out.println("Inserted: " + node.shortName());
				
				/* Check if the AstNode is a conditional or assignment.
				 * TODO: Create a visitor that searches for variable
				 * 		 declarations and assignments. */
				if(node instanceof VariableDeclaration) {
					/* Track variable being assigned. */
					VariableDeclaration vd = (VariableDeclaration) node;
					
					for(VariableInitializer vi : vd.getVariables()){
						AstNode variable = vi.getTarget();
						AstNode value = vi.getInitializer();
						
						/* TODO: Can anything else be initialized? Expressions? */
						if (variable instanceof Name) {
							Name name = (Name) variable;
							System.out.print("Variable: " + name.getIdentifier());
						}
						
						/* TODO: What node types can value be? */
						if (value instanceof Name) {
							Name name = (Name) value;
							System.out.println(", Value: " + name.getIdentifier());
						} else if (value instanceof StringLiteral) {
							StringLiteral literal = (StringLiteral) value;
							System.out.println(", Value: " + literal.getValue(false));
						} else {
							System.out.println("");
						}
						
						System.out.println("Variable: " + variable + ", Value: " + value);
					}
				} else if (node instanceof VariableInitializer) {
					throw new UnsupportedOperationException();
				} else if (node instanceof Assignment) {
					throw new UnsupportedOperationException();
				} else if (node instanceof IfStatement) {
					IfStatement ifStatement = (IfStatement) node;
					AstNode condition = ifStatement.getCondition();
					ConditionalTreeVisitor visitor = new ConditionalTreeVisitor();
					System.out.println("Condition Breakdown ---");
					condition.visit(visitor);
					System.out.println("End Condition Breakdown ---");
				}
			}
		}
		
		/* Compare the assignments sets to the comparisons sets. */
		for(String name : this.comparisons.getNames()) {
			EnumSet<SpecialType> types = this.comparisons.getSet(name);
			for(SpecialType type : types) {
				if(!this.assignments.setContains(name, type)){
					System.out.println("ALERT: Inserted special type check.");
				}
			}
		}
	}
	
    private class ConditionalTreeVisitor implements NodeVisitor {
    	
    	SpecialTypeMap comparisons;
    	
    	public ConditionalTreeVisitor () {
    		this.comparisons = RepairDiff.this.comparisons;
    	}
    	
        public boolean visit(AstNode node) {
        	
        	if(node instanceof InfixExpression) {
        		InfixExpression ie = (InfixExpression) node;
        		if(ie.getOperator() == Token.SHEQ || ie.getOperator() == Token.SHNE) {
        			if(ie.getLeft() instanceof Name && ie.getRight() instanceof Name) {
        				String left = ((Name) ie.getLeft()).getIdentifier();
        				String right = ((Name) ie.getRight()).getIdentifier();
        				
        				if(left.equals("undefined")) { 
        					System.out.println("Assignment of " + right + " to undefined."); 
        					this.comparisons.add(right, SpecialType.UNDEFINED);
                        }
        				else if(right.equals("undefined")) { 
        					System.out.println("Assignment of " + left + " to undefined.");  
        					this.comparisons.add(left, SpecialType.UNDEFINED);
                        }
        			}         			
        		}
        	}
        	System.out.println(node.shortName()); 
        	return true;
        }

    }

}