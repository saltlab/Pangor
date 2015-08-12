package ca.ubc.ece.salt.sdjsb.test.learning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.learning.apis.APIFactory;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordUse;
import ca.ubc.ece.salt.sdjsb.learning.apis.TopLevelAPI;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordDefinition.KeywordType;
import ca.ubc.ece.salt.sdjsb.learning.apis.KeywordUse.KeywordContext;
import ca.ubc.ece.salt.sdjsb.learning.pointsto.CSPredictor;
import ca.ubc.ece.salt.sdjsb.learning.pointsto.Predictor;

public class TestPredictor {
	@Test
	public void testPredictorRequiredPackagesWhenHasPackages() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();

		Map<KeywordUse, Integer> keywords = new HashMap<>();
		keywords.put(new KeywordUse(KeywordType.METHOD, KeywordContext.UNKNOWN, "parse", ChangeType.INSERTED), 1);
		keywords.put(new KeywordUse(KeywordType.METHOD, KeywordContext.UNKNOWN, "getUTCSeconds", ChangeType.INSERTED),
				1);
		keywords.put(new KeywordUse(KeywordType.PACKAGE, KeywordContext.UNKNOWN, "fs", ChangeType.INSERTED), 1);
		keywords.put(new KeywordUse(KeywordType.PACKAGE, KeywordContext.UNKNOWN, "path", ChangeType.INSERTED), 1);

		Predictor predictor = new CSPredictor(api, keywords);

		Set<String> packages = predictor.getRequiredPackagesNames();

		assertTrue(packages.contains("global"));
		assertTrue(packages.contains("fs"));
		assertTrue(packages.contains("path"));
		assertFalse(packages.contains("foo-package"));
	}

	@Test
	public void testPredictorRequiredPackagesWhenHasNoPackages() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();

		Map<KeywordUse, Integer> keywords = new HashMap<>();
		keywords.put(new KeywordUse(KeywordType.METHOD, KeywordContext.UNKNOWN, "parse", ChangeType.INSERTED), 1);
		keywords.put(new KeywordUse(KeywordType.METHOD, KeywordContext.UNKNOWN, "getUTCSeconds", ChangeType.INSERTED),
				1);

		Predictor predictor = new CSPredictor(api, keywords);

		Set<String> packages = predictor.getRequiredPackagesNames();

		/*
		 * global is always there
		 */
		assertEquals(1, packages.size());
		assertTrue(packages.contains("global"));
	}
}
