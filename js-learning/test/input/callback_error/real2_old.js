/*
 * marked: 558769 
*/

function() {
    var out, err;
    try {
        out = Parser.parse(tokens, opt);
    } catch (e) {
        err = e;
    }
    opt.highlight = highlight;
    return err ? callback(err) : callback(null, out);
}