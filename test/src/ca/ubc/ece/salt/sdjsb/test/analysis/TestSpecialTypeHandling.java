package ca.ubc.ece.salt.sdjsb.test.analysis;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.alert.Alert;
import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert;
import ca.ubc.ece.salt.sdjsb.alert.SpecialTypeAlert.SpecialType;
import ca.ubc.ece.salt.sdjsb.analysis.AbstractFlowAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeAnalysis;
import ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeLatticeElement;

public class TestSpecialTypeHandling extends TestAnalysis {
	
	private void runTest(String[] args, List<Alert> expectedAlerts, boolean printAlerts) throws Exception {
		AbstractFlowAnalysis<SpecialTypeLatticeElement> analysis = new SpecialTypeAnalysis();
		super.runTest(args, expectedAlerts, printAlerts, analysis);
	}

	@Test
	public void testUndefined() throws Exception{
		String src = "./test/input/special_type_handling/sth_undefined_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

}