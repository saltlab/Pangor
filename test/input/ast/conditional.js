/* ConditionalExpression AST test. */

var a = {yes: true, no: false},
    b = "cat",
    c = "dog";

var val = a.yes ? b : c;

console.log(val);
