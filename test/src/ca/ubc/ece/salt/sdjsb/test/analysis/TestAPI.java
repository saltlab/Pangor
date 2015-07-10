package ca.ubc.ece.salt.sdjsb.test.analysis;

import static org.junit.Assert.*;

import org.junit.Test;

import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.APIFactory;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.Keyword.KeywordType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.TopLevelAPI;

public class TestAPI {

	@Test
	public void testIsMemberOfWhenIsMember() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();

		assertTrue(api.isMemberOf(KeywordType.KEYWORD, "double"));
		assertTrue(api.isMemberOf(KeywordType.METHOD_NAME, "isPrototypeOf"));
		assertTrue(api.isMemberOf(KeywordType.FIELD, "length"));
		assertTrue(api.isMemberOf(KeywordType.CLASS, "Array"));
	}

	@Test
	public void testIsMemberOfWhenIsNotMember() {
		TopLevelAPI api = APIFactory.buildTopLevelAPI();

		assertFalse(api.isMemberOf(KeywordType.KEYWORD, "foo"));
		assertFalse(api.isMemberOf(KeywordType.METHOD_NAME, "bar"));
		assertFalse(api.isMemberOf(KeywordType.FIELD, "foo"));
		assertFalse(api.isMemberOf(KeywordType.CLASS, "Bar"));
	}
}
