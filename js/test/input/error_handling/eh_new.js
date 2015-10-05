/* Uncaught exception
 * Output: EH_UNCAUGHT_EXCEPTION */ 

function throwsException() {
	throw new Error();
}

try {
	throwsException();
}
catch (e) { }
