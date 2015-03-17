/* Special Type Handling: Branch condition is replaced with a delete operation.
 * Output: None */
InteractorDaemonizer.rpc.kill(function(err) {
    if (err) {
      if (!cb) Common.printError(err);
      return cb ? cb({msg : err}) : Common.exitCli(cst.ERROR_EXIT);
    }
    setTimeout(function() {
      if (!cb) Common.printOut('Interactor successfully killed');
      return cb ? cb(null, {msg : 'killed'}) : Common.exitCli(cst.SUCCESS_EXIT);
    }, 150);
    return false;
});
