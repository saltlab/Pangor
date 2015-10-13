/* Error Learning */

var name = process.argv[2];

function output(name, callback) {
	if(name) {
		callback(null, name);
	} else {
		callback("No name!", null);
	}
}

function print(err, name) {
	if(err) { 
		console.log(err);
	} else {
		console.log(name);
	}
}

output(name, print);
