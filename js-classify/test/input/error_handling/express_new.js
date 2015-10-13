function trim_prefix() {
  var c = path[layerPath.length];
  if (c && '/' != c && '.' != c) 
    return next(err);
  debug('trim prefix (%s) from url %s', removed, req.url);
  removed = layerPath;
  req.url = protohost + req.url.substr(protohost.length + removed.length);
  if (!fqdn && req.url[0] !== '/') {
    req.url = '/' + req.url;
    slashAdded = true;
  }
  if (removed.length && removed.substr(-1) === '/') {
    req.baseUrl = parentUrl + removed.substring(0, removed.length - 1);
  } else {
    req.baseUrl = parentUrl + removed;
  }
  debug('%s %s : %s', layer.handle.name || 'anonymous', layerPath, req.originalUrl);
  var arity = layer.handle.length;
  try {
    if (err && arity === 4) {
      layer.handle(err, req, res, next);
    } else if (!err && arity < 4) {
      layer.handle(req, res, next);
    } else {
      next(err);
    }
  }  catch (err) {
  next(err);
}
}
