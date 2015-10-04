/* Incorrect comparison 
 * Output: IC_FALSEY_TO_TYPE (a) */
var a = process.argv[2];

if(a === undefined) {
	a = "Missing input.";	
}

console.log(a);
