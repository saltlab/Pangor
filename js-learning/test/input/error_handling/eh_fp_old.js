/* Uncaught exception
 * Output: None */ 

function throwsException() {
	throw new Error();
}

try {
	throwsException();
}
catch (e) { }
