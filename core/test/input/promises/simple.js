/* Get the first command line arg. */
var name = process.argv[2];

/* Asynchronously print the name. */
function getUserDetails(user, cb){
		
		if(user) {	
			process.nextTick(function(){ 
				/* Call the callback with data. */
				cb(null,user);
			});
		}
		else { 
			/* Call the callback with error. */
			cb("Error: No name given.", null);
		}
}

/* Call the async function. */
getUserDetails(name, function (err, details) {

	if(err) {
		/* Error handler. */
		console.log(err);
	} else {
		/* Success handler. */
    console.log(details);
	}

});
