/* Statement AST pre-processing test. */

var name = "Sam";

function helloWorld(name) {

    var person = name || "World";

   console.log("Hello " + person + "!"); 

}

helloWorld(name);
