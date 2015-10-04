/* Call Bind and Apply Learning */

var object = {
	data: [ { name:"John", age:28 }, { name:"James", age:28 } ],
	print: function() {
		for(var i = 0; i < this.data.length; i++) {
			console.log(this.data[i].name);
		}
	}
}

function click(print) {
	print();
}

click(object.print.bind(object));
