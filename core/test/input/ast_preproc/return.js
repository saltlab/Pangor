/* ReturnStatement AST pre-processing test. */

var a = "Sam";

function helloWorld(a) {
    return a ? "Hello " + a + "!" : "Hello world!";
}

console.log(helloWorld(a));
