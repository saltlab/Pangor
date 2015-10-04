/* Test do loops. */

function helloWorld(animal) {
    "use strict";

    function speak(animal, noise) {
        console.log("The " + animal + " says " + noise + ".");
    }

    var i = 0, noise = "moo";
    do {
        speak(animal, noise);
        noise = noise + "o";
        i = i + 1;
    } while(i < 10)

}

var animal = "Cow";
helloWorld(animal);
