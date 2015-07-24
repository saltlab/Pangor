package ca.ubc.ece.salt.sdjsb.analysis.learning.ast;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode.ChangeType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.KeywordDefinition.KeywordType;
import ca.ubc.ece.salt.sdjsb.analysis.learning.apis.KeywordUse.KeywordContext;

public class KeywordFilter {

	public FilterType filterType;
	public KeywordType type;
	public KeywordContext context;
	public ChangeType changeType;
	public String pack;
	public String keyword;

	/**
	 * The default filter includes all keywords.
	 */
	public KeywordFilter() {
		this.filterType = FilterType.INCLUDE;
		this.type = KeywordType.UNKNOWN;
		this.context = KeywordContext.UNKNOWN;
		this.changeType = ChangeType.UNKNOWN;
		this.pack = "";
		this.keyword = "";
	}

	/**
	 * Create a new filter with all options.
	 * @param filterType
	 * @param type
	 * @param context
	 * @param changeType
	 * @param pack
	 * @param keyword
	 */
	public KeywordFilter(FilterType filterType, KeywordType type,
			KeywordContext context, ChangeType changeType, String pack,
			String keyword) {

		this.filterType = filterType;
		this.type = type;
		this.context = context;
		this.changeType = changeType;
		this.pack = pack;
		this.keyword = keyword;

	}

	/**
	 * INCLUDE includes all rows that match the filter.
	 * EXCLUDE excludes all rows that match the filter.
	 */
	public enum FilterType {
		INCLUDE,
		EXCLUDE
	}

	/**
	 * Factory method for building a package filter
	 *
	 * @param packageName
	 * @return a KeywordFilter
	 */
	public static KeywordFilter buildPackageFilter(String packageName) {
		return new KeywordFilter(FilterType.INCLUDE, KeywordType.UNKNOWN, KeywordContext.UNKNOWN, ChangeType.UNKNOWN,
				packageName, "");
	}

}