/* Not Defined: Nested in loop
 * Output: ND_UNDEFINED_VARIABLE (a) */

function helloWorld(a) {

    for(var i = 0; i < 5; i++) {
        
        if(a) {
            console.log("Hello " + a + "!");
        }
        else {
            console.log("Hello wold!");
        }

    }

}

function test() {
    if(true) {
        console.log("true");
    } else {
        console.log("false");
    }
    if(true) {
        console.log("true");
    } else {
        console.log("false");
    }
    if(true) {
        console.log("true");
    } else {
        console.log("false");
    }
    if(true) {
        console.log("true");
    } else {
        console.log("false");
    }
}

helloWorld("Elanor");
