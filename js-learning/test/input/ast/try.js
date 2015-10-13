/* TryStatement AST test. */

var a = "we are ";

function f(a) {
    try {
        console.log(a.val);
    }
    catch(e) {
        console.log(e);
    }
    finally {
        console.log("done");
    }
}

f(a);
