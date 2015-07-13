package ca.ubc.ece.salt.sdjsb.test.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.APIFactory;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.PackageAPI;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.TopLevelAPI;
import ca.ubc.ece.salt.sdjsb.analysis.prediction.PointsToPrediction;

public class TestPointsToPrediction {
	@Test
	public void testGetLikelyAPIWhenFindsMethod() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();
		PointsToPrediction predictor = new PointsToPrediction(api, null, null, null, null);
		
		PackageAPI predictionResult = predictor.getLikelyAPI(new Keyword(KeywordType.METHOD_NAME, "chmodSync"));
		assertEquals("fs", predictionResult.getIncludeName());
	}
	
	@Test
	public void testGetLikelyAPIWhenFindsField() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();
		PointsToPrediction predictor = new PointsToPrediction(api, null, null, null, null);
		
		PackageAPI predictionResult = predictor.getLikelyAPI(new Keyword(KeywordType.FIELD, "win32"));
		assertEquals("path", predictionResult.getIncludeName());
	}
	
	@Test
	public void testGetLikelyAPIWhenNotFound() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();
		PointsToPrediction predictor = new PointsToPrediction(api, null, null, null, null);
		
		PackageAPI predictionResult = predictor.getLikelyAPI(new Keyword(KeywordType.METHOD_NAME, "fooMethod"));
		assertNull(predictionResult);
	}
}
