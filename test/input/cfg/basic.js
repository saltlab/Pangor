/* Basic CFG test.
 * Output: ?  */

function helloWorld(name) {
    "use strict";

    if (name) {
        console.log("Hello " + name + "!");
    } else {
        console.log("Hello World!");
    }

    function speak(animal, noise) {
        console.log("The " + animal + " says " + noise + ".");
    }

}

var name;
helloWorld(name);
