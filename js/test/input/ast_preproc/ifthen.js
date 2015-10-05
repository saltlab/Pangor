/* If/Then/Else AST pre-processing test. */

var a = "Sam";

if(true) {
    a ? console.log("Hello " + a + "!") : console.log("Hello world!");
} else {
    a ? console.log("Goodbye " + a + "!") : console.log("Goodbye world!");
}
