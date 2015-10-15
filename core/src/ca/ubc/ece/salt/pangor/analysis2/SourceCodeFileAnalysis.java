package ca.ubc.ece.salt.pangor.analysis2;

import java.util.LinkedList;
import java.util.List;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;
import ca.ubc.ece.salt.pangor.cfg.CFG;

/**
 * Gathers facts (patterns, pre-conditions and anti-patterns) about changes to
 * a source code file.
 */
public abstract class SourceCodeFileAnalysis {

	/** Pre-conditions found by the analysis. **/
	private List<String> preConditions;

	/** Anti-patterns found by the analysis. **/
	private List<String> antiPatterns;

	/** Patterns found by the analysis. **/
	private List<String> patterns;

	public SourceCodeFileAnalysis() {
		this.preConditions = new LinkedList<String>();
		this.antiPatterns = new LinkedList<String>();
		this.patterns = new LinkedList<String>();
	}

	/**
	 * Perform a single-file analysis.
	 * @param root The script.
	 * @param cfgs The list of CFGs in the script (one for each function plus
	 * 			   one for the script).
	 */
	public abstract void analyze(ClassifiedASTNode root, List<CFG> cfgs) throws Exception;

	/**
	 * Adds a pre-condition fact.
	 * @param preCondition The pre-condition.
	 */
	protected void addPreCondition(String preCondition) {
		this.preConditions.add(preCondition);
	}

	/**
	 * @return The set of pre-condition facts generated during the analysis.
	 */
	public List<String> getPreConditions() {
		return this.preConditions;
	}

	/**
	 * Adds an anti-pattern fact.
	 * @param antiPattern The anti-pattern.
	 */
	protected void addAntiPattern(String antiPattern) {
		this.antiPatterns.add(antiPattern);
	}

	/**
	 * @return The set of anti-pattern facts generated during the analysis.
	 */
	public List<String> getAntiPatterns() {
		return this.antiPatterns;
	}

	/**
	 * Adds a pattern fact.
	 * @param pattern The pattern.
	 */
	protected void addPattern(String pattern) {
		this.patterns.add(pattern);
	}

	/**
	 * @return The set of pattern facts generated during the analysis.
	 */
	public List<String> getPatterns() {
		return this.patterns;
	}

}