/* Incorrect comparison 
 * Output: IC_FALSEY_TO_VALUE (a) */
var a = process.argv[2];

if(!a) {
	a = "Missing input.";	
}

console.log(a);
