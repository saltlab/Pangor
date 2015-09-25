/*Example taken from https://github.com/jonkemp/inline-css/pull/6/files*/
function inlineContent(src, options) {
    return new Promise(function(resolve, reject) {
        var content;

        if (!options.url) {
            reject('options.url is required');
        }

        extractCss(src, options, function (err, html, css) {
            if (err) {
                return reject(err);
            }

            css  = '...'

            content = inlineCss(html, css, options);
            resolve(content);
        });
    });

}

module.exports = function (html, options) {

    return new Promise(function(resolve, reject) {
        var opt = '...';

        inlineContent(html, opt)
            .then(function(data) { resolve(data); })
            .catch(function(err) { reject(err); })
    });

};