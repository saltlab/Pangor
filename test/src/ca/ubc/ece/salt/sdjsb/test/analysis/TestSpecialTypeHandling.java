package ca.ubc.ece.salt.sdjsb.test.analysis;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.cfg.CFGPrinter.Output;
import ca.ubc.ece.salt.sdjsb.checker.Alert;
import ca.ubc.ece.salt.sdjsb.checker.specialtype.SpecialTypeAlert;
import ca.ubc.ece.salt.sdjsb.checker.specialtype.SpecialTypeMap.SpecialType;

public class TestSpecialTypeHandling extends TestAnalysis {
	
	private void runTest(String[] args, List<Alert> expectedAlerts, Output output) throws Exception {
		List<String> classifiers = new LinkedList<String>();
		classifiers.add("ca.ubc.ece.salt.sdjsb.analysis.specialtype.SpecialTypeAnalysis");
		super.runTest(args, classifiers, expectedAlerts, output);
	}

	@Test
	public void testUndefined() throws Exception{
		String src = "./test/input/special_type_handling/sth_undefined_old.js";
		String dst = "./test/input/special_type_handling/sth_undefined_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));
		this.runTest(new String[] {src, dst}, expectedAlerts, Output.DOT);
	}

}