/* Special Type Handling: Variable Undefined While
 * Output: STH_TYPE_ERROR_UNDEFINED (a) */
var a;
while (a !== undefined && a > 1) {
    console.log(a);
}
