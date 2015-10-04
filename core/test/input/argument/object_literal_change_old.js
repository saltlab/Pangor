     var watcher = chokidar.watch(watch, {
       ignored       : ignored,
       persistent    : false,
       ignoreInitial : true
     });