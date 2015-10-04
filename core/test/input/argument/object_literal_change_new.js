     var watcher = chokidar.watch(watch, {
       ignored       : ignored,
       persistent    : true,
       ignoreInitial : true
     });