package ca.ubc.ece.salt.pangor.analysis.scope;

import java.util.List;
import java.util.Map;

import ca.ubc.ece.salt.gumtree.ast.ClassifiedASTNode;

/**
 * Provides type/function hierarchy information.
 */
public interface Scope<T> {

	/**
	 * @return The scope above this scope (i.e., the scope for the parent of
	 * this function or class).
	 */
	Scope<T> getParent();

	/**
	 * @return The AST node of the class, script or function of this scope.
	 */
	T getScope();

	/**
	 * @return The variables declared in the scope.
	 */
	Map<String, T> getVariables();

	/**
	 * @return The globals declared in the scope.
	 */
	Map<String, T> getGlobals();

	/**
	 * @return The scopes of the child functions.
	 */
	List<Scope<T>> getChildren();

	/**
	 * @return The unique identifier for this scope.
	 */
	String getIdentity();

	/**
	 * Starting with the current scope, search the tree upwards until the
	 * identifier is found (or not found).
	 * @param variable The variable to find.
	 * @return The Name node where the variable is declared.
	 */
	T getVariableDeclaration(String variable);

	/**
	 * @param function The function to find.
	 * @return The scope for the function, or null if the function is not a
	 * 		   function of the function/script/class for this scope.
	 */
	Scope<T> getFunctionScope(ClassifiedASTNode function);

	/**
	 * Checks if a variable is in a local scope by checking the scope tree
	 * until we get to the global scope.
	 * @param variable The variable to check.
	 * @return True if the variable is defined in a local scope.
	 */
	boolean isLocal(String variable);

	/**
	 * Checks if a variable is in a global scope by checking parents in the
	 * scope tree until we get to the root.
	 * @param variable The variable to check.
	 * @return True if the variable is defined in a global scope.
	 */
	boolean isGlobal(String variable);

}
