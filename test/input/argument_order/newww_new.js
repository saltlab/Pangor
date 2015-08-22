function(type, arg, skip, limit, next) {
  var opts = {};
  if (typeof type === 'object') {
    opts = type;
    type = opts.type;
  }
  return next(null, browse[type]);
}