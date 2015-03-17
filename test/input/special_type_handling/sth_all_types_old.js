/* Special Type Handling: Variable {Undefined,Null,NaN,Zero,Blank}
 * Output: STH_TYPE_ERROR_UNDEFINED (a) 
 * 		   STH_TYPE_ERROR_NULL (b) 
 * 		   STH_TYPE_ERROR_NAN (c) 
 * 		   STH_TYPE_ERROR_ZERO (c) 
 * 		   STH_TYPE_ERROR_BLANK (e) */
var a = undefined, b = null, c = NaN, d = 0, e = '';
console.log(a);
console.log(b);
console.log(c);
console.log(d);
console.log(e);
