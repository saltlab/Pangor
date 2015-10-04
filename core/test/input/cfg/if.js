/* Basic CFG test. */

function helloWorld(name) {
    "use strict";

    if (name) {
        console.log("Hello " + name + "!");
    } else {
        console.log("Hello!");
        console.log("What is your name?");
    }

}

var name = "Scruffy";
helloWorld(name);
