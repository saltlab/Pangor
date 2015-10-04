Gulp.prototype.watch = function(glob, opt, fn) {
  if (!fn) {
    fn = opt;
    opt = null;
  }

  // array of tasks given
  if (Array.isArray(fn)) {
    return vfs.watch(glob, opt, function() {
      this.start.apply(this, fn);
    }.bind(this));
  }

  return vfs.watch(glob, opt, fn);
};