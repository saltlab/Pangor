/* ReturnStatement AST pre-processing test. */

var a = "Sam";

helloWorld = function (a) {
    return a ? "Hello " + a + "!" : "Hello world!";
}

console.log(helloWorld(a));
