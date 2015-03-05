package ca.ubc.ece.salt.sdjsb.test;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.checker.SpecialTypeMap.SpecialType;
import ca.ubc.ece.salt.sdjsb.checker.alert.Alert;
import ca.ubc.ece.salt.sdjsb.checker.alert.SpecialTypeAlert;

public class TestSpecialTypeHandling extends TestSDJSB {
	
	@Test
	public void testSample(){
		String src = "./test/input/sample-conf-old.js";
		String dst = "./test/input/sample-conf-new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new SpecialTypeAlert("STH", "a", SpecialType.UNDEFINED));

		this.runTest(new String[] {src, dst}, expectedAlerts);
	}

}
