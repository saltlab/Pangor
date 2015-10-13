/* Not Defined: Nested in loop
 * Output: ND_UNDEFINED_VARIABLE (a) */

function helloWorld(a) {

    for(i = 0; i < 5; i++) {
        
        if(a) {
            console.log("Hello " + a + "!");
        }
        else {
            console.log("Hello wold!");
        }

    }

}

helloWorld("Elanor");
