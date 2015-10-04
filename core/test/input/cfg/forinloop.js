/* Test for in loops. */

function helloWorld(animals) {
    "use strict";

    function speak(animal, noise) {
        console.log("The " + animal + " says " + noise + ".");
    }

    var noises = ["moo","moose","neigh","thump"], i = 0;
    for(var noise in noises) {
        speak(animals[i], noises[noise]);
        i = i + 1;
    }

}

var animals = ["cow","moose","horse","buffalo"];
helloWorld(animals);
