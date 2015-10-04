function(promise) {
  if (this._trampolineEnabled) {
    AsyncSettlePromises(promise);
  } else {
    setTimeout(function() {
  promise._settlePromises();
}, 0);
  }
}