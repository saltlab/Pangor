/* Special Type Handling: Variable Undefined
 * Output: STH_TYPE_ERROR_UNDEFINED (a) */

var a;

function log(a) {
    if(a === undefined) return;
    console.log(a);
}

log(a);
