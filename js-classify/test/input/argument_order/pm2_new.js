CLI._operate = function(action_name, process_name, envs, cb) {
  // Make sure all options exist
  if(typeof envs == 'function'){
    cb = envs;
    envs = {};
  }
};