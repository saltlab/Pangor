/* Repair Pattern Learning */

function printMessage(message, callback) {
	if(message) {
    console.log(message);
		callback(null);
	}
	else {
		callback("No message to print.");
	}
}

function donePrinting(err) {
    if(err) {
        console.log("Error: " + err);
    }
    console.log("Finished!");
}

function doNothing() {
	var a = 5;
	var b = 7;
	a = a + b;
}

printMessage(process.argv[2], donePrinting);
