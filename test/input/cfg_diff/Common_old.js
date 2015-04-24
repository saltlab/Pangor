Common.extend = function(origin, add){
  // Don't do anything if add isn't an object
  if (!add || typeof add != 'object') return origin;

  //Ignore PM2's set environment variables from the nested env
  var keysToIgnore = ['name', 'exec_mode', 'env', 'pm_cwd', 'exec_interpreter', 'pm_exec_path', 'node_args', 'pm_out_log_path', 'pm_err_log_path', 'pm_pid_path', 'pm_id', 'status', 'pm_uptime', 'created_at', 'started_inside', 'unstable_restarts', 'restart_time', 'pm_id', 'axm_actions', 'pmx_module', 'command', 'watch', 'versioning', 'vizion_runing', 'MODULE_DEBUG'];

  var keys = Object.keys(add);
  var i = keys.length;
  while (i--) {
    //Only copy stuff into the env that we don't have already.
    if(keysToIgnore.indexOf(keys[i]) == -1 && add[keys[i]] != '[object Object]')
      origin[keys[i]] = add[keys[i]];
  }
  return origin;
};
