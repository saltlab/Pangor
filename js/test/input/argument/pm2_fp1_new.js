  God.killProcess = function(pid, pm2_env, cb) {
    if (!pid) return cb({msg : 'no pid passed or null'});

    var mode = pm2_env.exec_mode;

    try {
      if(pm2_env.treekill !== true)
        process.kill(parseInt(pid));
      else {
        if (mode.indexOf('cluster') === 0)
          treekill(parseInt(pid));
        else
          process.kill(-(parseInt(pid)), 'SIGINT');
      }
    } catch(e) {
      console.error('%s pid can not be killed', pid, e);
      return;
    }

    return God.processIsDead(pid, cb);
  };