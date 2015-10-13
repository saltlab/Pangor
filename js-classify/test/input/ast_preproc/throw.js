/* ThrowStatement AST pre-processing test. */

var a = "Sam";

function helloWorld(a) {
    throw a ? "Hello " + a + "!" : "Hello world!";
}

helloWorld(a);
