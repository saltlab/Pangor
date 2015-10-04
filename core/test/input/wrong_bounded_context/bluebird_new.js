function(promise) {
  if (this._trampolineEnabled) {
    AsyncSettlePromises.call(this, promise);
  } else {
    setTimeout(function() {
  promise._settlePromises();
}, 0);
  }
}