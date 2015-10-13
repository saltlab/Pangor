/* Special Type Handling: Variable Undefined Element Get
 * Output: STH_TYPE_ERROR_UNDEFINED (a) */
var a, b = { dog: "woof", cat: "meow" };
if(a != undefined) {
    console.log(b[a]);
}
