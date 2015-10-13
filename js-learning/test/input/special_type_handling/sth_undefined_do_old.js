/* Special Type Handling: Variable Undefined Do
 * Output: STH_TYPE_ERROR_UNDEFINED (a) */
var a = "Iteration ", i = 1;
do {
    console.log(a + i);
    if (i == 5) {
        a = undefined;
    }
    i++;
} while (i <= 10);
