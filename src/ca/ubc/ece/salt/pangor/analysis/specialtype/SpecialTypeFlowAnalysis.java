package ca.ubc.ece.salt.pangor.analysis.specialtype;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ScriptNode;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.pangor.analysis.AnalysisUtilities;
import ca.ubc.ece.salt.pangor.analysis.classify.ClassifierDataSet;
import ca.ubc.ece.salt.pangor.analysis.flow.PathSensitiveFlowAnalysis;
import ca.ubc.ece.salt.pangor.analysis.scope.Scope;
import ca.ubc.ece.salt.pangor.batch.AnalysisMetaInformation;
import ca.ubc.ece.salt.pangor.cfg.CFGEdge;
import ca.ubc.ece.salt.pangor.cfg.CFGNode;
import ca.ubc.ece.salt.pangor.classify.alert.ClassifierAlert;
import ca.ubc.ece.salt.pangor.classify.alert.SpecialTypeAlert.SpecialType;

/**
 * A change-sensitive analysis that finds special type repairs in JavaScript.
 *
 * Not special type path:
 * 	1. New edge conditions that check that an identifier is not a special type.
 *  2. That identifier is used after the check, but before it is assigned and
 *     that use was in the original program.
 *  *. If the variable is used or assigned, we remove it from the map (after
 *     generating an alert if needed).
 *
 * Special type path:
 * 	1. New edge conditions that check that an identifier is a special type.
 *  2. That identifier is assigned before it is used. The assignment is
 *     inserted and the use is in the original program.
 *
 */
public class SpecialTypeFlowAnalysis extends PathSensitiveFlowAnalysis<ClassifierAlert, ClassifierDataSet, SpecialTypeLatticeElement> {

	/** Stores the possible special type check repairs. */
	private Map<String, List<SpecialTypeCheckResult>> specialTypeCheckResults;

	public SpecialTypeFlowAnalysis(ClassifierDataSet dataSet, AnalysisMetaInformation ami) {
		super(dataSet, ami);
		this.specialTypeCheckResults = new HashMap<String, List<SpecialTypeCheckResult>>();
	}

	/**
	 * @return The set of possible special type check repairs (or
	 * anti-patterns if this is the source file analysis.
	 */
	public Map<String, List<SpecialTypeCheckResult>> getSpecialTypeCheckResults() {
		return this.specialTypeCheckResults;
	}

	@Override
	public SpecialTypeLatticeElement entryValue(ScriptNode function) {
		return new SpecialTypeLatticeElement();
	}

	@Override
	public void transfer(CFGEdge edge, SpecialTypeLatticeElement sourceLE, Scope scope) {

		AstNode condition = (AstNode)edge.getCondition();
		if(condition == null) return;

		/* Check if condition has an inserted special type check and whether
		 * the check evaluates to true or false. */
		SpecialTypeVisitor visitor = new SpecialTypeVisitor(condition);
		condition.visit(visitor);

		/* Add any special type checks to the lattice element. */
		for(SpecialTypeCheck specialTypeCheck : visitor.getSpecialTypeChecks()) {

			/* Is the identifier definitely a special type on this path or
			 * definitely not a special type on this path?
			 */
			if(specialTypeCheck.isSpecialType) {

				/* Make sure this identifier wasn't introduced as a variable. */
                AstNode declaration = scope.getVariableDeclaration(specialTypeCheck.identifier);
				if(declaration != null && declaration.getChangeType() == ChangeType.INSERTED) return;

				/* Is the identifier already in the map? */
				if(sourceLE.specialTypes.containsKey(specialTypeCheck.identifier)) {

					/* Add the special type to the list of types the
					 * identifier could possibly be (based on the type check). */
					List<SpecialType> couldBe = sourceLE.specialTypes.get(specialTypeCheck.identifier);
					if(!couldBe.contains(specialTypeCheck.specialType)) couldBe.add(specialTypeCheck.specialType);

				}
				else {

					/* Add the special type to the list of types the
					 * identifier could possibly be (based on the type check). */
					LinkedList<SpecialType> couldBe = new LinkedList<SpecialType>();
					couldBe.add(specialTypeCheck.specialType);
                    sourceLE.specialTypes.put(specialTypeCheck.identifier, couldBe);

				}

			}
			else {

				/* Make sure this identifier wasn't introduced as a variable. */
                AstNode declaration = scope.getVariableDeclaration(specialTypeCheck.identifier);
				if(declaration != null && declaration.getChangeType() == ChangeType.INSERTED) return;

				/* Is the identifier already in the map? */
				if(sourceLE.nonSpecialTypes.containsKey(specialTypeCheck.identifier)) {

					/* Add the special type to the list of types the
					 * identifier could not possibly be (based on the type check). */
					List<SpecialType> couldBe = sourceLE.nonSpecialTypes.get(specialTypeCheck.identifier);
					if(!couldBe.contains(specialTypeCheck.specialType)) couldBe.add(specialTypeCheck.specialType);

				}
				else {

					/* Add the special type to the list of types the
					 * identifier could not possibly be (based on the type check). */
					LinkedList<SpecialType> couldNotBe = new LinkedList<SpecialType>();
					couldNotBe.add(specialTypeCheck.specialType);
                    sourceLE.nonSpecialTypes.put(specialTypeCheck.identifier, couldNotBe);
				}
			}
		}

	}

	@Override
	public void transfer(CFGNode node, SpecialTypeLatticeElement sourceLE, Scope scope) {

		AstNode statement = (AstNode)node.getStatement();

		/* Loop through the moved or unchanged identifiers that are used in
		 * this statement. */
        Set<String> usedIdentifiers = AnalysisUtilities.getUsedIdentifiers(statement);
        for(String identifier : sourceLE.nonSpecialTypes.keySet()) {

        	/* Make sure this is a valid path... */
        	if(sourceLE.specialTypes.containsKey(identifier)) continue;

        	/* Is the identifier in our "definitely not a special type" list? */
        	if(usedIdentifiers.contains(identifier)) {

        		/* Check that this identifier hasn't been newly assigned to
        		 * the special type we are checking. */
        		SpecialType assignedTo = sourceLE.assignments.get(identifier);
        		if(assignedTo != SpecialType.FALSEY) {

        			List<SpecialType> specialTypes = sourceLE.nonSpecialTypes.get(identifier);
        			for(SpecialType specialType : specialTypes) {

        				if(assignedTo != specialType) {

                            /* Trigger an alert! */
        					List<SpecialTypeCheckResult> identifierResults = null;
        					if(this.specialTypeCheckResults.containsKey(identifier)) {
        						identifierResults = this.specialTypeCheckResults.get(identifier);
        					}
        					else {
        						identifierResults = new LinkedList<SpecialTypeCheckResult>();
								this.specialTypeCheckResults.put(identifier, identifierResults);
        					}
							identifierResults.add(new SpecialTypeCheckResult(identifier, specialType));

        				}

        			}

        		}

        	}
        }

        /* Check if the statement has an assignment. */
        List<Pair<String, AstNode>> assignments = SpecialTypeAnalysisUtilities.getIdentifierAssignments(statement);

        for(Pair<String, AstNode> assignment : assignments) {

        	SpecialType specialType = SpecialTypeAnalysisUtilities.getSpecialType(assignment.getValue());

        	/* Store the assignment if it is a new special type assignment. */
        	if(specialType != null && (assignment.getValue().getChangeType() == ChangeType.INSERTED
        			|| assignment.getValue().getChangeType() == ChangeType.REMOVED
        			|| assignment.getValue().getChangeType() == ChangeType.UPDATED)) {
        		sourceLE.assignments.put(assignment.getKey(), specialType);
        	}

        	/* Remove the assignment (if it exists) if it is not (any old
        	 * assignments are no longer relevant). */
        	else {
        		sourceLE.assignments.remove(assignment.getKey());
        	}

        	/* Remove the identifier from the special type set (if it exists). */
        	if(sourceLE.nonSpecialTypes.containsKey(assignment.getKey())) {

        		/* Remove the identifier. */
        		sourceLE.nonSpecialTypes.remove(assignment.getKey());

        	}

        }

	}

	@Override
	public SpecialTypeLatticeElement copy(SpecialTypeLatticeElement le) {
		return SpecialTypeLatticeElement.copy(le);
	}

	/**
	 * Stores an identifier that was used in a new special type check, and then
	 * used on the 'guaranteed not a special type' path.
	 */
	public class SpecialTypeCheckResult {

		public String identifier;
		public SpecialType specialType;

		public SpecialTypeCheckResult(String identifier, SpecialType specialType) {
			this.identifier = identifier;
			this.specialType = specialType;
		}

		@Override
		public boolean equals(Object o) {

			if(!(o instanceof SpecialTypeCheckResult)) return false;

			SpecialTypeCheckResult cec = (SpecialTypeCheckResult) o;

			if(this.identifier.equals(cec.identifier) && this.specialType.equals(cec.specialType)) return true;

			return false;

		}

		@Override
		public int hashCode() {
			return (this.identifier + "-" + this.specialType).hashCode();
		}
	}

}
