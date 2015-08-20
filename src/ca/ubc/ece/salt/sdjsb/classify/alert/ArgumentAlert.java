package ca.ubc.ece.salt.sdjsb.classify.alert;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.batch.AnalysisMetaInformation;

/**
 * Alert to notify changes on the arguments of a function call
 */
public class ArgumentAlert extends ClassifierAlert {
	public ChangeType changeType;
	public String argumentChanged;

	public ArgumentAlert(AnalysisMetaInformation ami, String functionName, String argumentChanged,
			ChangeType changeType) {
		super(ami, functionName, "ARGUMENT", changeType.toString());
		this.argumentChanged = argumentChanged;
		this.changeType = changeType;

		if (argumentChanged.equals("~objectLiteral~"))
			this.subtype = "~objectLiteral~";
	}

	@Override
	public String getAlertDescription() {
		return "The function call '" + this.functionName + "' has a new parameters list. '" + argumentChanged + "' was "
				+ changeType.toString() + ".";
	}

	@Override
	public String getAlertExplanation() {
		return "The function was being called with a undesired parameters list. The repair is done by updating the parameters list.";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ArgumentAlert && super.equals(o)) {
			ArgumentAlert aa = (ArgumentAlert) o;
			return (this.functionName.equals(aa.functionName) && this.argumentChanged.equals(aa.argumentChanged));
		}
		return false;
	}

}
