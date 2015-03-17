/* Special Type Handling: Variable Undefined Conditional
 * Output: STH_TYPE_ERROR_UNDEFINED (a) */
var a = undefined;
var b = a !== undefined ? a : "Hello World!";
