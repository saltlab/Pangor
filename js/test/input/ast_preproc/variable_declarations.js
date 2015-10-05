/* ReturnStatement AST pre-processing test. */

var a = "Sam",
    b = "John",
    c = "Fred";

function helloWorld(a) {
    var d = "Yes",
        e = "No",
        f = "Maybe";
    return a ? "Hello " + a + "!" : "Hello world!";
}

console.log(helloWorld(a));

var g = "Don't expand";
