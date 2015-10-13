/* Repair Pattern Learning: Test keyword changes. */

var user = process.argv[2];

function callb() {
	console.log("callback");
}

if(user !== null && user !== undefined && user !== "" && user !== 0) {
	console.log("Hello " + user + "!");

	try {
		callb();
	}
	catch (e) {
		console.log("Caught exception");	
	}
}
