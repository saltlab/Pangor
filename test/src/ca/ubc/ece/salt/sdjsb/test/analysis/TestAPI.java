package ca.ubc.ece.salt.sdjsb.test.analysis;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.APIFactory;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.TopLevelAPI;

public class TestAPI {

	@Test
	public void testIsMemberOfWhenIsMember() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();

		assertNotNull(api.isMemberOf(KeywordType.RESERVED, "double"));
		assertNotNull(api.isMemberOf(KeywordType.METHOD_NAME, "isPrototypeOf"));
		assertNotNull(api.isMemberOf(KeywordType.FIELD, "length"));
		assertNotNull(api.isMemberOf(KeywordType.CLASS, "Array"));
	}

	@Test
	public void testIsMemberOfWhenIsNotMember() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();

		assertNull(api.isMemberOf(KeywordType.RESERVED, "foo"));
		assertNull(api.isMemberOf(KeywordType.METHOD_NAME, "bar"));
		assertNull(api.isMemberOf(KeywordType.FIELD, "foo"));
		assertNull(api.isMemberOf(KeywordType.CLASS, "Bar"));
	}

	@Test
	public void testUseLikelihoodWhenFieldIsUsed() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();

		Map<Keyword, Integer> insertedKeywords = new HashMap<>();
		insertedKeywords.put(new Keyword(KeywordType.METHOD_CALL, "bar"), 3);
		insertedKeywords.put(new Keyword(KeywordType.FIELD, "Infinity"), 2);
		insertedKeywords.put(new Keyword(KeywordType.FIELD, "foo"), 1);

		double likelihood = api.getUseLikelihood(insertedKeywords, null, null, null);

		assertTrue(likelihood == 1);
	}

	@Test
	public void testUseLikelihoodWhenClassIsUsed() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();

		Map<Keyword, Integer> insertedKeywords = new HashMap<>();
		insertedKeywords.put(new Keyword(KeywordType.METHOD_CALL, "bar"), 3);
		insertedKeywords.put(new Keyword(KeywordType.CLASS, "Date"), 2);
		insertedKeywords.put(new Keyword(KeywordType.FIELD, "foo"), 1);

		double likelihood = api.getUseLikelihood(insertedKeywords, null, null, null);

		assertTrue(likelihood == 1);
	}
}
