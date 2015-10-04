/* Function body AST pre-processing test. */

var a = "Sam";

function helloWorld(a) {
    a ? console.log("Hello " + a + "!") : console.log("Hello world!");
}

helloWorld(a);
