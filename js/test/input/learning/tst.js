function f() { 
	var a = "local-A"; 
	console.log("We can, of course access " + a + " within f."); 
	function getA() { return "Because of closure, we can access " + a + " within a method of f."; }
	console.log(getA());
	this.a = "field-A";
};

f();

console.log("Accessing 'a' outside of f (without creating a new object) yields " + f.a + ".");
console.log("We cannot call f.getA() from outside f because f is not an object.");

function g() {
	console.log("Executing 'new g()' executes g() and returns an instance of the object backing g");
	this.b = "field-B";
}

var x = new g();
console.log("If we create a new object with 'new g()', we can access " + x.b + ".");

function h() {
	var c = "local-C";
	this.c = c;
	this.d = "field-D";

	this.getC = function () { return c; }
}

var y = new h();
console.log("We can access  " + y.c + " by assigning it to a field."); 
y.c = "modified-C";
y.d = "modified-D"
var z = new h();
z.c = "modified-C";
console.log("When we create a new object with 'new h()', " + z.c + " will always be the same.");
console.log("We can make a 'private' variable with local variables (since we have closure). Example: " + z.c  + " vs. " + z.getC() + ".");
