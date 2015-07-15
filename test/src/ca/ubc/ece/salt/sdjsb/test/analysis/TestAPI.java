package ca.ubc.ece.salt.sdjsb.test.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.APIFactory;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.AbstractAPI;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;

public class TestAPI {

	@Test
	public void testGetFirstKeywordWhenIsMember() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();

		assertNotNull(api.getFirstKeyword(KeywordType.RESERVED, "double"));
		assertNotNull(api.getFirstKeyword(KeywordType.METHOD_NAME, "isPrototypeOf"));
		assertNotNull(api.getFirstKeyword(KeywordType.FIELD, "length"));
		assertNotNull(api.getFirstKeyword(KeywordType.CLASS, "Array"));
	}

	@Test
	public void testGetFirstKeywordWhenIsNotMember() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();

		assertNull(api.getFirstKeyword(KeywordType.RESERVED, "foo"));
		assertNull(api.getFirstKeyword(KeywordType.METHOD_NAME, "bar"));
		assertNull(api.getFirstKeyword(KeywordType.FIELD, "foo"));
		assertNull(api.getFirstKeyword(KeywordType.CLASS, "Bar"));
	}

	@Test
	public void testGetAllKeywordsWhenIsMemberOnDifferentAPIs() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();

		List<Keyword> keywordsList = api.getAllKeywords(new Keyword(KeywordType.EVENT, "open"));
		List<String> APIsNames = extractAPIsFromKeywordList(keywordsList);

		/*
		 * open event is member of ReadStream and WriteStream class
		 */
		assertEquals(2, keywordsList.size());
		assertTrue(APIsNames.contains("WriteStream"));
		assertTrue(APIsNames.contains("ReadStream"));
		assertFalse(APIsNames.contains("Math"));
	}

	@Test
	public void testUseLikelihoodWhenFieldIsUsed() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();

		Map<Keyword, Integer> insertedKeywords = new HashMap<>();
		insertedKeywords.put(new Keyword(KeywordType.METHOD_CALL, "bar"), 3);
		insertedKeywords.put(new Keyword(KeywordType.FIELD, "Infinity"), 2);
		insertedKeywords.put(new Keyword(KeywordType.FIELD, "foo"), 1);

		double likelihood = api.getUseLikelihood(insertedKeywords, null, null, null);

		assertTrue(likelihood == 1);
	}

	@Test
	public void testUseLikelihoodWhenClassIsUsed() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();

		Map<Keyword, Integer> insertedKeywords = new HashMap<>();
		insertedKeywords.put(new Keyword(KeywordType.METHOD_CALL, "bar"), 3);
		insertedKeywords.put(new Keyword(KeywordType.CLASS, "Date"), 2);
		insertedKeywords.put(new Keyword(KeywordType.FIELD, "foo"), 1);

		double likelihood = api.getUseLikelihood(insertedKeywords, null, null, null);

		assertTrue(likelihood == 1);
	}

	/*
	 * Test helper
	 */
	private List<String> extractAPIsFromKeywordList(List<Keyword> keywordsList) {
		List<String> APIsNames = new ArrayList<>();

		for (Keyword keyword : keywordsList) {
			APIsNames.add(keyword.api.getName());
		}

		return APIsNames;
	}
}
