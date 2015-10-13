/* Special Type Handling: Variable Used (Dereference)
 * Output: None */
var a, b;
a.field = {};
if (a.field.value !== undefined) {
    b = 1 + a.field.value;
}
