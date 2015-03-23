/* Callback Parameter: Missing Error Parameter
 * Output: CBP_MISSING_ERROR_PARAM (err) */
function printMessage(message, callback) {
    console.log(message);
}

function donePrinting() {
    console.log("Finished!");
}

printMessage("Hello World!", donePrinting);
