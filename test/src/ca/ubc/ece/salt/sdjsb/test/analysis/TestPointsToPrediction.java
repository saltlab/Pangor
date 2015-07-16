package ca.ubc.ece.salt.sdjsb.test.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.APIFactory;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.TopLevelAPI;
import ca.ubc.ece.salt.sdjsb.analysis.prediction.PointsToPrediction;

public class TestPointsToPrediction {
	/*
	 * 'parse' is present on both 'path' and 'Date' APIs 'normalize' and 'posix'
	 * are present on 'path'
	 *
	 * Expected result: 'path'
	 */
	@Test
	public void testGetKeywordWhenAmbiguousOnPath() {
		Map<Keyword, Integer> insertedKeywords = new HashMap<>();
		insertedKeywords.put(new Keyword(KeywordType.METHOD_NAME, "parse"), 1);

		Map<Keyword, Integer> removedKeywords = new HashMap<>();
		insertedKeywords.put(new Keyword(KeywordType.METHOD_NAME, "normalize"), 1);

		Map<Keyword, Integer> updatedKeywords = new HashMap<>();
		insertedKeywords.put(new Keyword(KeywordType.FIELD, "posix"), 1);

		TopLevelAPI api = APIFactory.buildTopLevelAPI();
		PointsToPrediction predictor = new PointsToPrediction(api, insertedKeywords, removedKeywords, updatedKeywords,
				null);

		Keyword keyword = predictor.getKeyword(KeywordType.METHOD_NAME, "parse");

		assertNotNull(keyword);
		assertEquals("path", keyword.api.getName());
		assertEquals("path", keyword.getPackageName());
	}

	/*
	 * 'parse' is present on both 'path' and 'Date' APIs
	 * 'getMinutes' and 'getUTCSeconds' are present on 'Date'
	 *
	 * Expected result: 'Date'
	 */
	@Test
	public void testGetKeywordWhenAmbiguousOnDate() {
		Map<Keyword, Integer> insertedKeywords = new HashMap<>();
		insertedKeywords.put(new Keyword(KeywordType.METHOD_NAME, "parse"), 1);

		Map<Keyword, Integer> removedKeywords = new HashMap<>();
		insertedKeywords.put(new Keyword(KeywordType.METHOD_NAME, "getMinutes"), 1);

		Map<Keyword, Integer> updatedKeywords = new HashMap<>();
		insertedKeywords.put(new Keyword(KeywordType.METHOD_NAME, "getUTCSeconds"), 1);

		TopLevelAPI api = APIFactory.buildTopLevelAPI();
		PointsToPrediction predictor = new PointsToPrediction(api, insertedKeywords, removedKeywords, updatedKeywords,
				null);

		Keyword keyword = predictor.getKeyword(KeywordType.METHOD_NAME, "parse");

		assertNotNull(keyword);
		assertEquals("Date", keyword.api.getName());
		assertEquals("global", keyword.getPackageName());
	}

	/*
	 * 'win32' is present only on 'path' API
	 */
	@Test
	public void testGetKeywordWhenNotAmbiguous() {
		Map<Keyword, Integer> insertedKeywords = new HashMap<>();
		insertedKeywords.put(new Keyword(KeywordType.FIELD, "win32"), 1);

		TopLevelAPI api = APIFactory.buildTopLevelAPI();
		PointsToPrediction predictor = new PointsToPrediction(api, insertedKeywords, null, null, null);

		Keyword keyword = predictor.getKeyword(KeywordType.FIELD, "win32");

		assertNotNull(keyword);
		assertEquals("path", keyword.api.getName());
	}

	/*
	 * fooMethod was not given on the list of inserted/updated/removed/unchange
	 * keywords
	 */
	@Test(expected = RuntimeException.class)
	public void testGetKeywordOnInvalidInput() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();
		PointsToPrediction predictor = new PointsToPrediction(api, null, null, null, null);

		Keyword keyword = predictor.getKeyword(KeywordType.METHOD_NAME, "fooMethod");
	}
}
