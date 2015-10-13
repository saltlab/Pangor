/* Special Type Handling: Field Undefined
 * Output: STH_TYPE_ERROR_UNDEFINED (a.field.value) */
var a;
a.field = {};
a.field.value = undefined;
if (a.field.value !== undefined) {
    console.log(a.field.value);
}
