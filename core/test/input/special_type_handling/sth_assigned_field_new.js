/* Special Type Handling: Field Undefined
 * Output: None */
var a;
a.field = {};
a.field.value = undefined;
if (a.field.value !== undefined) {
    console.log(a.field.value);
}
