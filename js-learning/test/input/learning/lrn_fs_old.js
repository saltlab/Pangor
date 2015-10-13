/* File System Repair Learning Pattern. */

var fs = require('fs');
var fs2 = require('fs');
var testDir = "/Users/qhanam/Documents/workspace_gumtree/ca.ubc.ece.salt.sdjsb/test/output/";
var testFile = "HelloWorld.txt";
var user = "Bob";

function writeHello(name) {
	var fd = fs.openSync(testDir + testFile);
	fs.writeSync(fd, "Hello World!");
}

function sayHello(name) {
	console.log("Hello " + name);
}

if(fs.existsSync(testDir)) {
	writeHello(user);
}

sayHello(user);
