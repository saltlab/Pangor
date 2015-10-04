/* Test try/catch block. */

function doStuff() {

    var x, y = 10, z = 0;

    try {
        x = y / z;
        if(x === Infinity) throw "Divide by zero.";
        console.log("Result = " + x);
        return;
    }
    catch(err) {
        console.log("Error: " + err);
    }
    finally {
        console.log("Finished Execution.");
    }

}

doStuff();
