/* Unhandled Callback Error: Unchecked Error Parameter
 * Output: CB_UNCHECKED_ERROR_PARAM (err) */
function printMessage(message, callback) {
    console.log(null, message);
}

function donePrinting(err) {
    console.log("Finished!");
}

printMessage("Hello World!", donePrinting);
