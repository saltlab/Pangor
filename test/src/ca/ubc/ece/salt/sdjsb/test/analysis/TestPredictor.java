package ca.ubc.ece.salt.sdjsb.test.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.APIFactory;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.TopLevelAPI;
import ca.ubc.ece.salt.sdjsb.analysis.prediction.CSPredictor;
import ca.ubc.ece.salt.sdjsb.analysis.prediction.Predictor;

public class TestPredictor {
	@Test
	public void testPredictorRequiredPackagesWhenHasPackages() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();

		Map<Keyword, Integer> insertedKeywords = new HashMap<>();
		insertedKeywords.put(new Keyword(KeywordType.METHOD_NAME, "parse"), 1);
		insertedKeywords.put(new Keyword(KeywordType.PACKAGE, "fs"), 1);
		insertedKeywords.put(new Keyword(KeywordType.PACKAGE, "path"), 1);
		insertedKeywords.put(new Keyword(KeywordType.METHOD_NAME, "getUTCSeconds"), 1);

		Predictor predictor = new CSPredictor(api, insertedKeywords, null, null, null);

		Set<String> packages = predictor.getRequiredPackagesNames();

		assertTrue(packages.contains("global"));
		assertTrue(packages.contains("fs"));
		assertTrue(packages.contains("path"));
		assertFalse(packages.contains("foo-package"));
	}

	@Test
	public void testPredictorRequiredPackagesWhenHasNoPackages() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();

		Map<Keyword, Integer> insertedKeywords = new HashMap<>();
		insertedKeywords.put(new Keyword(KeywordType.METHOD_NAME, "parse"), 1);
		insertedKeywords.put(new Keyword(KeywordType.METHOD_NAME, "getUTCSeconds"), 1);

		Predictor predictor = new CSPredictor(api, insertedKeywords, null, null, null);

		Set<String> packages = predictor.getRequiredPackagesNames();

		/*
		 * global is always there
		 */
		assertEquals(1, packages.size());
		assertTrue(packages.contains("global"));
	}
}
