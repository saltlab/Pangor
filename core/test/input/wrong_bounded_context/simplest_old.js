/* Wrong Bounded Context: 
 * Output: AST_WRONG_BOUNDED_CONTEXT: The function call 'foo' has a new bounded context. 
 * Important to change arguments so GumTree is not confused */

/* POSITIVE */
// No arguments
foo1(1);

// Arguments
foo2(1, 2);

// Method on a object
object.foo3(3, 4);	
