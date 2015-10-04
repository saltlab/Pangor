/*
 * marked: 558769 
*/
function(err) {
    if (err) {
        opt.highlight = highlight;
        return callback(err);
    }
    var out;
    try {
        out = Parser.parse(tokens, opt);
    } catch (e) {
        err = e;
    }
    opt.highlight = highlight;
    return err ? callback(err) : callback(null, out);
}