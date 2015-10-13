/* Test for loops. */

function helloWorld(animal) {
    "use strict";

    function speak(animal, noise) {
        console.log("The " + animal + " says " + noise + ".");
    }

    var noise = "moo";
    for(var i = 0; i < 10; i++) {
        speak(animal, noise);
        noise = noise + "o";
        i = i + 1;
    }

}

var animal = "Cow";
helloWorld(animal);
