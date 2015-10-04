/* Special Type Handling: Variable Function 
 * Output: STH_TYPE_ERROR_FUNCTION (a) */
var a;
var b;
if (typeof a === 'function') {
	a();
}
if (typeof b == 'function') {
	b();
}
