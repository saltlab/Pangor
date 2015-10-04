/* Get the first command line arg. */
var name = process.argv[2];

/* Asynchronously print the name. */
function getUserDetails(user) {

	/* Create the promise. */
	return new Promise(function (resolve, reject) {

		if(user) {
			process.nextTick(function(){ 
				/* Call the success handler. */
				resolve(user); 
			});
		}
		else { 
			/* Call the error handler. */
			reject("Error: No name given.");
		}

	});

}

/* Generate the promise and register the handler. */
getUserDetails(name)

	.then( 
		/* Success handler. */
		function(details) {
			console.log(details);
		})

	.catch( 
		/* Error handler. */
		function(err) {
			console.log(err);
		});
