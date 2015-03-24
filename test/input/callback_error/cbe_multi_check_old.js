/* Unhandled Callback Error: Unchecked Error Parameter
 * Output: None */
function printMessage(message, callback) {
    console.log(null, message);
}

function donePrinting(err) {
    if(err) {
        var a = 7;
        console.log("Error1!");
        console.log("Error2!");
    }
    console.log("Finished!");
}

printMessage("Hello World!", donePrinting);
