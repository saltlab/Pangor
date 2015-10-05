/* Repair Pattern Learning */

function printMessage(message, callback) {
	if(message) {
    console.log(message);
		callback(null);
	}
}

function donePrinting(err) {
    if(err) {
        console.log("Error: " + err);
    }
    console.log("Finished!");
}

function doNothing() {
}

printMessage(process.argv[2], donePrinting);
