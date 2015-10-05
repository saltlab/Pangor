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

/* Get the first command line arg. */
function getUserDetailsPromised(param0) {

    return new Promise(function (resolve, reject) {
        getUserDetails(param0, function(err,data){
                    if(err !== null)
                        return reject(err);
                    resolve(data);
                });
    });

}

/* Call the async function. */
getUserDetailsPromised(name)

	.then(
		function (details) {
			/* Success handler. */
			console.log(details);
		}, 
		function (err) {
			/* Error handler. */
			console.log(err);
		});
