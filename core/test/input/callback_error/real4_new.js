/*
 * node-browserify: 2156173
 */
function(err, file) {
  if (!file) 
    return cb(new Error('module ' + JSON.stringify(id) + ' not found from ' + JSON.stringify(parent.filename)));
  if (self._ignore[file]) 
    return cb(null, emptyModulePath);
  if (self._external[file]) 
    return cb(null, file, true);
}