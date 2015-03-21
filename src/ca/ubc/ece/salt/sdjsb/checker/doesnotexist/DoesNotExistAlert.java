package ca.ubc.ece.salt.sdjsb.checker.doesnotexist;

import ca.ubc.ece.salt.sdjsb.checker.Alert;
import ca.ubc.ece.salt.sdjsb.checker.doesnotexist.DoesNotExistCheckerUtilities.NameType;

public class DoesNotExistAlert extends Alert {
	
	private String sourceIdentifier;
	private String destinationIdentifier;
	private NameType nameType;
	
	public DoesNotExistAlert(String type, String sourceIdentifier, String destinationIdentifier, NameType nameType) {
		super(type, "UNDEFINED_ERROR_" + nameType.toString());
		this.sourceIdentifier = sourceIdentifier;
		this.destinationIdentifier = destinationIdentifier;
		this.nameType = nameType;
	}

	@Override
	public String getAlertDescription() {
		return "A possible undefined " + this.nameType + " was repaired by changing the identifier '" + this.sourceIdentifier + "' to '" + this.destinationIdentifier + "'.";
	}

	@Override
	public String getAlertExplanation() {
		return "A variable, field or method was renamed.";
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof DoesNotExistAlert && super.equals(o)) {
			DoesNotExistAlert sta = (DoesNotExistAlert) o;
			return this.sourceIdentifier.equals(sta.sourceIdentifier) && this.destinationIdentifier.equals(sta.destinationIdentifier) && this.nameType == sta.nameType;
		}
		return false;
	}

}
