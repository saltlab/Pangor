function doSomething(a, b, cb) {
	if (a > b) {
		cb(null, true);
	} else {
		cb(err, false);
	}
}