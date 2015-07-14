package ca.ubc.ece.salt.sdjsb.test.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.APIFactory;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.TopLevelAPI;
import ca.ubc.ece.salt.sdjsb.analysis.prediction.PointsToPrediction;

public class TestPointsToPrediction {
	@Test
	public void testGetLikelyAPIWhenFindsMethod() {
		Keyword keyword = new Keyword(KeywordType.METHOD_NAME, "chmodSync");
		TopLevelAPI api = APIFactory.buildTopLevelAPI();
		PointsToPrediction predictor = new PointsToPrediction(api, null, null, null, null);

		boolean predicted = predictor.findLikelyAPI(keyword);

		assertTrue(predicted);
		assertEquals("fs", keyword.api.getName());
	}

	@Test
	public void testGetLikelyAPIWhenFindsField() {
		Keyword keyword = new Keyword(KeywordType.FIELD, "win32");
		TopLevelAPI api = APIFactory.buildTopLevelAPI();
		PointsToPrediction predictor = new PointsToPrediction(api, null, null, null, null);

		boolean predicted = predictor.findLikelyAPI(keyword);

		assertTrue(predicted);
		assertEquals("path", keyword.api.getName());
	}

	@Test
	public void testGetLikelyAPIWhenNotFound() {
		Keyword keyword = new Keyword(KeywordType.METHOD_NAME, "fooMethod");
		TopLevelAPI api = APIFactory.buildTopLevelAPI();
		PointsToPrediction predictor = new PointsToPrediction(api, null, null, null, null);

		boolean predicted = predictor.findLikelyAPI(keyword);

		assertFalse(predicted);
		assertNull(keyword.api);
	}
}
