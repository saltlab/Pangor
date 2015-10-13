package ca.ubc.ece.salt.pangor.test.learning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.pangor.learning.apis.APIFactory;
import ca.ubc.ece.salt.pangor.learning.apis.KeywordDefinition.KeywordType;
import ca.ubc.ece.salt.pangor.learning.apis.KeywordUse;
import ca.ubc.ece.salt.pangor.learning.apis.KeywordUse.KeywordContext;
import ca.ubc.ece.salt.pangor.learning.apis.TopLevelAPI;
import ca.ubc.ece.salt.pangor.learning.pointsto.PointsToPrediction;

public class TestPointsToPrediction {
	/*
	 * 'parse' is present on both 'path' and 'Date' APIs 'normalize' and 'posix'
	 * are present on 'path' BUT path IS NOT imported
	 *
	 * Expected result: 'Date' (because path was not imported)
	 */
//	@Test
//	public void testGetKeywordWhenAmbiguousOnPathButPathIsNotImported() {
//		Map<KeywordUse, Integer> keywords = new HashMap<>();
//		keywords.put(new KeywordUse(KeywordType.METHOD, KeywordContext.UNKNOWN, "parse", ChangeType.INSERTED), 1);
//		keywords.put(new KeywordUse(KeywordType.METHOD, KeywordContext.UNKNOWN, "normalize", ChangeType.REMOVED), 1);
//		keywords.put(new KeywordUse(KeywordType.METHOD, KeywordContext.UNKNOWN, "posix", ChangeType.UPDATED), 1);
//
//		TopLevelAPI api = APIFactory.buildTopLevelAPI();
//		PointsToPrediction predictor = new PointsToPrediction(api, keywords);
//
//		KeywordUse keyword = predictor.getKeyword(KeywordType.METHOD, "parse");
//
//		assertNotNull(keyword);
//		assertEquals("Date", keyword.api.getName());
//		assertEquals("global", keyword.getPackageName());
//	}

	/*
	 * 'parse' is present on both 'path' and 'Date' APIs 'normalize' and 'posix'
	 * are present on 'path' AND relevant packages are imported
	 *
	 * Expected result: 'path'
	 */
//	@Test
//	public void testGetKeywordWhenAmbiguousOnPath() {
//		Map<KeywordUse, Integer> keywords = new HashMap<>();
//		keywords.put(new KeywordUse(KeywordType.METHOD, KeywordContext.UNKNOWN, "parse", ChangeType.INSERTED), 1);
//		keywords.put(new KeywordUse(KeywordType.PACKAGE, KeywordContext.UNKNOWN, "path", ChangeType.INSERTED), 1);
//		keywords.put(new KeywordUse(KeywordType.METHOD, KeywordContext.UNKNOWN, "normalize", ChangeType.REMOVED), 1);
//		keywords.put(new KeywordUse(KeywordType.METHOD, KeywordContext.UNKNOWN, "posix", ChangeType.UPDATED), 1);
//
//		TopLevelAPI api = APIFactory.buildTopLevelAPI();
//		PointsToPrediction predictor = new PointsToPrediction(api, keywords);
//
//		KeywordUse keyword = predictor.getKeyword(KeywordType.METHOD, "parse");
//
//		assertNotNull(keyword);
//		assertEquals("path", keyword.api.getName());
//		assertEquals("path", keyword.getPackageName());
//	}

	/*
	 * 'parse' is present on both 'path' and 'Date' APIs 'getMinutes' and
	 * 'getUTCSeconds' are present on 'Date' AND relevant packages are imported
	 *
	 * Expected result: 'Date'
	 */
	@Test
	public void testGetKeywordWhenAmbiguousOnDate() {
		Map<KeywordUse, Integer> keywords = new HashMap<>();
		keywords.put(new KeywordUse(KeywordType.METHOD, KeywordContext.UNKNOWN, "parse", ChangeType.INSERTED), 1);
		keywords.put(new KeywordUse(KeywordType.PACKAGE, KeywordContext.UNKNOWN, "path", ChangeType.INSERTED), 1);
		keywords.put(new KeywordUse(KeywordType.METHOD, KeywordContext.UNKNOWN, "getMinutes", ChangeType.REMOVED), 1);
		keywords.put(new KeywordUse(KeywordType.METHOD, KeywordContext.UNKNOWN, "getUTCSeconds", ChangeType.UPDATED),
				1);

		TopLevelAPI api = APIFactory.buildTopLevelAPI();
		PointsToPrediction predictor = new PointsToPrediction(api, keywords);

		KeywordUse keyword = predictor.getKeyword(KeywordType.METHOD, "parse");

		assertNotNull(keyword);
		assertEquals("Date", keyword.api.getName());
		assertEquals("global", keyword.getPackageName());
	}

	/*
	 * 'win32' is present only on 'path' API AND relevant packages are imported
	 */
//	@Test
//	public void testGetKeywordWhenNotAmbiguous() {
//		Map<KeywordUse, Integer> keywords = new HashMap<>();
//		keywords.put(new KeywordUse(KeywordType.FIELD, KeywordContext.UNKNOWN, "win32", ChangeType.INSERTED), 1);
//		keywords.put(new KeywordUse(KeywordType.PACKAGE, KeywordContext.UNKNOWN, "path", ChangeType.INSERTED), 1);
//
//		TopLevelAPI api = APIFactory.buildTopLevelAPI();
//		PointsToPrediction predictor = new PointsToPrediction(api, keywords);
//
//		KeywordUse keyword = predictor.getKeyword(KeywordType.FIELD, "win32");
//
//		assertNotNull(keyword);
//		assertEquals("path", keyword.api.getName());
//	}

	/*
	 * 'win32' is present only on 'path' API BUT package is not imported
	 */
	@Test
	public void testGetKeywordWhenNotAmbiguousButNotImported() {
		Map<KeywordUse, Integer> keywords = new HashMap<>();
		keywords.put(new KeywordUse(KeywordType.FIELD, KeywordContext.UNKNOWN, "win32", ChangeType.INSERTED), 1);

		TopLevelAPI api = APIFactory.buildTopLevelAPI();
		PointsToPrediction predictor = new PointsToPrediction(api, keywords);

		KeywordUse keyword = predictor.getKeyword(KeywordType.FIELD, "win32");

		assertNull(keyword);
	}

	/*
	 * fooMethod was not given on the list of inserted/updated/removed/unchanged
	 * keywords
	 */
	@Test(expected = RuntimeException.class)
	public void testGetKeywordOnInvalidInput() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();
		PointsToPrediction predictor = new PointsToPrediction(api, null);

		@SuppressWarnings("unused")
		KeywordUse keyword = predictor.getKeyword(KeywordType.METHOD, "fooMethod");
	}
}
