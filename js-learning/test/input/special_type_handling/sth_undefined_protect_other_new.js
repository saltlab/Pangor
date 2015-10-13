/* Special Type Handling: Variable Undefined
 * Output: STH_TYPE_ERROR_UNDEFINED (a) */

var a;
var logging = true;

function helloWorld(err, a) {
    if(!a && logging) return console.log("Logging... " + a);
    console.log("Hello " + a + "!");
}

helloWorld(null, a);
