/* Statement Learning */

function testFunction(path, callback) {
	if(path) {
		callback(null, "True");
	}
	callback(null, "False");
}

testFunction(true, function (error, data) {
		console.log(data);
	});

cnt = 10;
while(cnt > 0) {
	cnt--;
}

console.log(cnt);
