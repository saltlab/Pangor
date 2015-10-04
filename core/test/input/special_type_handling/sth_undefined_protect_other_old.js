/* Special Type Handling: Variable Undefined
 * Output: STH_TYPE_ERROR_UNDEFINED (a) */

var a;

function helloWorld(err, a) {
    console.log("Hello " + a + "!");
}

helloWorld(null, a);
