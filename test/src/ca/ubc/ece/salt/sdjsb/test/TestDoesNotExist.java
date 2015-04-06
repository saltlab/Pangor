package ca.ubc.ece.salt.sdjsb.test;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.checker.Alert;
import ca.ubc.ece.salt.sdjsb.checker.doesnotexist.DoesNotExistCheckerUtilities.NameType;
import ca.ubc.ece.salt.sdjsb.checker.doesnotexist.DoesNotExistAlert;

public class TestDoesNotExist extends TestSDJSB {
	
	private void runTest(String[] args, List<Alert> expectedAlerts, boolean printAlerts) {
		List<String> checkers = new LinkedList<String>();
		checkers.add("ca.ubc.ece.salt.sdjsb.checker.doesnotexist.DoesNotExistChecker");
		super.runTest(args, checkers, expectedAlerts, printAlerts);
	}
	
	@Test
	public void testDoesNotExist(){
		String src = "./test/input/does_not_exist/ActionMethods_old.js";
		String dst = "./test/input/does_not_exist/ActionMethods_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		expectedAlerts.add(new DoesNotExistAlert("DNE", "action.data.action_name", "action.action_name", NameType.FIELD));
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

	@Test
	public void testInsert(){
		String src = "./test/input/does_not_exist/ProcessContainer_unknown_old.js";
		String dst = "./test/input/does_not_exist/ProcessContainer_unknown_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, true);
	}

	/**
	 *  This test fails. In general this checker is not precise enough for
	 *  practical use (too many false positives).
	 */
	@Test
	public void testMove(){
		String src = "./test/input/does_not_exist/ProcessContainer_old.js";
		String dst = "./test/input/does_not_exist/ProcessContainer_new.js";
		List<Alert> expectedAlerts = new LinkedList<Alert>();
		this.runTest(new String[] {src, dst}, expectedAlerts, false);
	}

}
