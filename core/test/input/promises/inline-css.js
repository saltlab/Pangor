/*Example taken from https://github.com/jonkemp/inline-css/pull/6/files*/
function inlineContent(src, options, callback) {
    assert.ok(options.url, 'options.url is required');
    extractCss(src, options, function (err, html, css) {
        if (err) {
            return callback(err);
        }

        css  = '...'

        inlineCssWithCb(html, css, options, callback);
    });
}

module.exports = function (html, options, callback) {
    var opt = '...';

    inlineContent(html, opt, function (err, content) {
        if (err) {
            callback(err);
        } else {
            callback(null, content);
        }
    });
};

function inlineCssWithCb(html, css, options, callback) {
    var content;

    try {
        content = inlineCss(html, css, options);
        callback(null, content);
    } catch (err) {
        callback(err);
    }
}
