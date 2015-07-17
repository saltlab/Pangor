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
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.KeywordDefinition;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.KeywordDefinition.KeywordType;

public class TestAPI {
	@Test
	public void testGetPackageNameWhenHasAPackage() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();

		KeywordDefinition keyword = api.getFirstKeyword(KeywordType.FIELD, "bytesWritten");
		assertEquals("WriteStream", keyword.api.getName());
		assertEquals("fs", keyword.getPackageName());
	}

	@Test
	public void testGetPackageNameWhenIsAPackage() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();

		KeywordDefinition keyword = api.getFirstKeyword(KeywordType.PACKAGE, "fs");
		assertEquals("fs", keyword.api.getName());
		assertEquals("fs", keyword.getPackageName());
	}

	@Test
	public void testGetPackageNameWhenIsAClassOfAPackage() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();

		KeywordDefinition keyword = api.getFirstKeyword(KeywordType.CLASS, "WriteStream");

		/*
		 * It seems strange that WriteStream api's is WriteStream. However, the
		 * logic is: the WriteStream keyword points to the WriteStream ClassAPI,
		 * which makes sense, as the keyword is stored within the ClassAPI class
		 */
		assertEquals("WriteStream", keyword.api.getName());
		assertEquals("fs", keyword.getPackageName());
	}

	@Test
	public void testGetPackageNameWhenIsGlobal() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();

		KeywordDefinition keyword = api.getFirstKeyword(KeywordType.METHOD, "getDate");
		assertEquals("Date", keyword.api.getName());
		assertEquals("global", keyword.getPackageName());
	}


	@Test
	public void testGetFirstKeywordWhenIsMember() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();

		assertNotNull(api.getFirstKeyword(KeywordType.RESERVED, "double"));
		assertNotNull(api.getFirstKeyword(KeywordType.METHOD, "isPrototypeOf"));
		assertNotNull(api.getFirstKeyword(KeywordType.FIELD, "length"));
		assertNotNull(api.getFirstKeyword(KeywordType.CLASS, "Array"));
	}

	@Test
	public void testGetFirstKeywordWhenIsNotMember() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();

		assertNull(api.getFirstKeyword(KeywordType.RESERVED, "foo"));
		assertNull(api.getFirstKeyword(KeywordType.METHOD, "bar"));
		assertNull(api.getFirstKeyword(KeywordType.FIELD, "foo"));
		assertNull(api.getFirstKeyword(KeywordType.CLASS, "Bar"));
	}

	@Test
	public void testGetAllKeywordsWhenIsMemberOnDifferentAPIs() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();

		List<KeywordDefinition> keywordsList = api.getAllKeywords(new KeywordDefinition(KeywordType.EVENT, "open"));
		List<String> APIsNames = extractAPIsFromKeywordList(keywordsList);

		/*
		 * 'open' event is member of ReadStream and WriteStream class, but not
		 * of Math
		 */
		assertEquals(2, keywordsList.size());
		assertTrue(APIsNames.contains("WriteStream"));
		assertTrue(APIsNames.contains("ReadStream"));
		assertFalse(APIsNames.contains("Math"));
	}

	@Test
	public void testGetAllKeywordsWhenIsNotMember() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();
		List<KeywordDefinition> keywordsList = api
				.getAllKeywords(new KeywordDefinition(KeywordType.EVENT, "foo-event"));

		assertEquals(0, keywordsList.size());
	}

	@Test
	public void testUseLikelihoodWhenFieldIsUsed() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();

		Map<KeywordDefinition, Integer> insertedKeywords = new HashMap<>();
		insertedKeywords.put(new KeywordDefinition(KeywordType.METHOD, "bar"), 3);
		insertedKeywords.put(new KeywordDefinition(KeywordType.FIELD, "Infinity"), 2);
		insertedKeywords.put(new KeywordDefinition(KeywordType.FIELD, "foo"), 1);

		double likelihood = api.getUseLikelihood(insertedKeywords, null, null, null);

		assertTrue(likelihood == 1);
	}

	@Test
	public void testUseLikelihoodWhenClassIsUsed() {
		AbstractAPI api = APIFactory.buildTopLevelAPI();

		Map<KeywordDefinition, Integer> insertedKeywords = new HashMap<>();
		insertedKeywords.put(new KeywordDefinition(KeywordType.METHOD, "bar"), 3);
		insertedKeywords.put(new KeywordDefinition(KeywordType.CLASS, "Date"), 2);
		insertedKeywords.put(new KeywordDefinition(KeywordType.FIELD, "foo"), 1);

		double likelihood = api.getUseLikelihood(insertedKeywords, null, null, null);

		assertTrue(likelihood == 1);
	}

	/*
	 * Test helper
	 */
	private List<String> extractAPIsFromKeywordList(List<KeywordDefinition> keywordsList) {
		List<String> APIsNames = new ArrayList<>();

		for (KeywordDefinition keyword : keywordsList) {
			APIsNames.add(keyword.api.getName());
		}

		return APIsNames;
	}
}
