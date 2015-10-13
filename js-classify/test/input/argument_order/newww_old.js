function(type, arg, skip, limit, next) {
  return next(null, browse[type]);
}