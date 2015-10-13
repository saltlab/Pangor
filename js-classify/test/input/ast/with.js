/* WithStatement AST test. */

function f() {
    var a = {b: 2, c: 3};
    with(a) { 
        console.log(b + c);
    }
}

f();
