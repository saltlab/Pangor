/* Statement Learning */

function testFunction(path, callback) {
	if(path) {
		callback(null, "True");
		return;
	}
	callback(null, "False");
}

testFunction(true, function (error, data) {
		console.log(data);
	});

var cnt = 10;
while(true) {
	if(cnt == 5) break;
	cnt--;
	continue;
}

console.log(cnt);
