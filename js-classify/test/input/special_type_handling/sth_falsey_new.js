/* Special Type Handling: Falsey
 * Output: STH_TYPE_ERROR_FALSEY (a)
 *         STH_TYPE_ERROR_FALSEY (b)
 *         STH_TYPE_ERROR_FALSEY (c) */
var a, b, c;
if(a) {
    console.log(a);
}
if(b && c) {
    console.log(b);
    console.log(c);
}
