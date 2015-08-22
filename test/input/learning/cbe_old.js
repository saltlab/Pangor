/* Error Learning */

var name = process.argv[2];

function output(name, callback) {
	if(name) {
		callback(null, name);
	} else {
		callback("No name!", null);
	}
}

function print(name) {
	console.log(name);
}

output(name, print);
