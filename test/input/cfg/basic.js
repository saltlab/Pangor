/* Basic CFG test. */

function helloWorld(name) {
    "use strict";

    if (name) {
        console.log("Hello " + name + "!");
    } else {
        console.log("Hello World!");

        if(true) {
            console.log("Go Canucks!");
        }
    }

    function speak(animal, noise) {
        console.log("The " + animal + " says " + noise + ".");
    }

}

var name;
helloWorld(name);
