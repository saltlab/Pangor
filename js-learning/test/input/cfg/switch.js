/* Test for in loops. */

function helloWorld(animals) {
    "use strict";

    function speak(animal) {
        var noise;
        switch(animal) {
            case "cow":
                noise = "moo";
                break;
            case "moose":
                noise = "moose";
                break;
            case "horse":
                noise = "neigh";
                break;
            case "buffalo":
            default:
                noise = "thump";
        }

        console.log("The " + animal + " says " + noise + ".");
    }

    for(var key in animals) {
        speak(animals[key]);
    }

}

var animals = ["cow","moose","horse","buffalo"];
helloWorld(animals);
