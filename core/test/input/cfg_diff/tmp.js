UX.describeTable = function(process) {
  var table = new Table({
  style: {
  'padding-left': 1, 
  head: ['cyan', 'bold'], 
  compact: true}});
  var pm2_env = process.pm2_env;
  var created_at = 'N/A';
  try {
    if (pm2_env.created_at != null) 
      created_at = new Date(pm2_env.created_at).toISOString();
  }  catch (e) {
  throw new Error(pm2_env.created_at + ' is not a valid date: ' + e.message, e.fileName, e.lineNumber);
}
  console.log('Describing process with id %d - name %s', pm2_env.pm_id, pm2_env.name);
  if (!pm2_env.args && pm2_env.node_args.length != 0 && pm2_env.watch && (pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': ''}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': pm2_env.node_args}, {
  'watch & reload': chalk.green.bold('\u2714')}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': timeSince(pm2_env.pm_uptime)}, {
  'created at': created_at});
  } else if (!pm2_env.args && pm2_env.node_args.length != 0 && pm2_env.watch && !(pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': ''}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': pm2_env.node_args}, {
  'watch & reload': chalk.green.bold('\u2714')}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': 0}, {
  'created at': created_at});
  } else if (!pm2_env.args && pm2_env.node_args.length != 0 && !pm2_env.watch && (pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': ''}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': pm2_env.node_args}, {
  'watch & reload': '\u2718'}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': timeSince(pm2_env.pm_uptime)}, {
  'created at': created_at});
  } else if (!pm2_env.args && pm2_env.node_args.length != 0 && !pm2_env.watch && !(pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': ''}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': pm2_env.node_args}, {
  'watch & reload': '\u2718'}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': 0}, {
  'created at': created_at});
  } else if (!pm2_env.args && !pm2_env.node_args.length != 0 && pm2_env.watch && (pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': ''}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': ''}, {
  'watch & reload': chalk.green.bold('\u2714')}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': timeSince(pm2_env.pm_uptime)}, {
  'created at': created_at});
  } else if (!pm2_env.args && !pm2_env.node_args.length != 0 && pm2_env.watch && !(pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': ''}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': ''}, {
  'watch & reload': chalk.green.bold('\u2714')}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': 0}, {
  'created at': created_at});
  } else if (!pm2_env.args && !pm2_env.node_args.length != 0 && !pm2_env.watch && (pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': ''}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': ''}, {
  'watch & reload': '\u2718'}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': timeSince(pm2_env.pm_uptime)}, {
  'created at': created_at});
  } else if (!pm2_env.args && !pm2_env.node_args.length != 0 && !pm2_env.watch && !(pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': ''}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': ''}, {
  'watch & reload': '\u2718'}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': 0}, {
  'created at': created_at});
  } else if (pm2_env.args && typeof pm2_env.args == 'string' && pm2_env.node_args.length != 0 && pm2_env.watch && (pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (JSON.parse(pm2_env.args.replace(/'/g, '"'))).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': pm2_env.node_args}, {
  'watch & reload': chalk.green.bold('\u2714')}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': timeSince(pm2_env.pm_uptime)}, {
  'created at': created_at});
  } else if (pm2_env.args && typeof pm2_env.args == 'string' && pm2_env.node_args.length != 0 && pm2_env.watch && !(pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (JSON.parse(pm2_env.args.replace(/'/g, '"'))).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': pm2_env.node_args}, {
  'watch & reload': chalk.green.bold('\u2714')}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': 0}, {
  'created at': created_at});
  } else if (pm2_env.args && typeof pm2_env.args == 'string' && pm2_env.node_args.length != 0 && !pm2_env.watch && (pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (JSON.parse(pm2_env.args.replace(/'/g, '"'))).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': pm2_env.node_args}, {
  'watch & reload': '\u2718'}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': timeSince(pm2_env.pm_uptime)}, {
  'created at': created_at});
  } else if (pm2_env.args && typeof pm2_env.args == 'string' && pm2_env.node_args.length != 0 && !pm2_env.watch && !(pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (JSON.parse(pm2_env.args.replace(/'/g, '"'))).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': pm2_env.node_args}, {
  'watch & reload': '\u2718'}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': 0}, {
  'created at': created_at});
  } else if (pm2_env.args && typeof pm2_env.args == 'string' && !pm2_env.node_args.length != 0 && pm2_env.watch && (pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (JSON.parse(pm2_env.args.replace(/'/g, '"'))).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': ''}, {
  'watch & reload': chalk.green.bold('\u2714')}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': timeSince(pm2_env.pm_uptime)}, {
  'created at': created_at});
  } else if (pm2_env.args && typeof pm2_env.args == 'string' && !pm2_env.node_args.length != 0 && pm2_env.watch && !(pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (JSON.parse(pm2_env.args.replace(/'/g, '"'))).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': ''}, {
  'watch & reload': chalk.green.bold('\u2714')}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': 0}, {
  'created at': created_at});
  } else if (pm2_env.args && typeof pm2_env.args == 'string' && !pm2_env.node_args.length != 0 && !pm2_env.watch && (pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (JSON.parse(pm2_env.args.replace(/'/g, '"'))).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': ''}, {
  'watch & reload': '\u2718'}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': timeSince(pm2_env.pm_uptime)}, {
  'created at': created_at});
  } else if (pm2_env.args && typeof pm2_env.args == 'string' && !pm2_env.node_args.length != 0 && !pm2_env.watch && !(pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (JSON.parse(pm2_env.args.replace(/'/g, '"'))).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': ''}, {
  'watch & reload': '\u2718'}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': 0}, {
  'created at': created_at});
  } else if (pm2_env.args && !typeof pm2_env.args == 'string' && pm2_env.node_args.length != 0 && pm2_env.watch && (pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (pm2_env.args).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': pm2_env.node_args}, {
  'watch & reload': chalk.green.bold('\u2714')}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': timeSince(pm2_env.pm_uptime)}, {
  'created at': created_at});
  } else if (pm2_env.args && !typeof pm2_env.args == 'string' && pm2_env.node_args.length != 0 && pm2_env.watch && !(pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (pm2_env.args).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': pm2_env.node_args}, {
  'watch & reload': chalk.green.bold('\u2714')}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': 0}, {
  'created at': created_at});
  } else if (pm2_env.args && !typeof pm2_env.args == 'string' && pm2_env.node_args.length != 0 && !pm2_env.watch && (pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (pm2_env.args).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': pm2_env.node_args}, {
  'watch & reload': '\u2718'}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': timeSince(pm2_env.pm_uptime)}, {
  'created at': created_at});
  } else if (pm2_env.args && !typeof pm2_env.args == 'string' && pm2_env.node_args.length != 0 && !pm2_env.watch && !(pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (pm2_env.args).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': pm2_env.node_args}, {
  'watch & reload': '\u2718'}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': 0}, {
  'created at': created_at});
  } else if (pm2_env.args && !typeof pm2_env.args == 'string' && !pm2_env.node_args.length != 0 && pm2_env.watch && (pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (pm2_env.args).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': ''}, {
  'watch & reload': chalk.green.bold('\u2714')}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': timeSince(pm2_env.pm_uptime)}, {
  'created at': created_at});
  } else if (pm2_env.args && !typeof pm2_env.args == 'string' && !pm2_env.node_args.length != 0 && pm2_env.watch && !(pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (pm2_env.args).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': ''}, {
  'watch & reload': chalk.green.bold('\u2714')}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': 0}, {
  'created at': created_at});
  } else if (pm2_env.args && !typeof pm2_env.args == 'string' && !pm2_env.node_args.length != 0 && !pm2_env.watch && (pm2_env.pm_uptime && pm2_env.status == 'online')) {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (pm2_env.args).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': ''}, {
  'watch & reload': '\u2718'}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': timeSince(pm2_env.pm_uptime)}, {
  'created at': created_at});
  } else {
    safe_push(table, {
  'status': colorStatus(pm2_env.status)}, {
  'name': pm2_env.name}, {
  'id': pm2_env.pm_id}, {
  'path': pm2_env.pm_exec_path}, {
  'args': (pm2_env.args).join(' ')}, {
  'exec cwd': pm2_env.pm_cwd}, {
  'error log path': pm2_env.pm_err_log_path}, {
  'out log path': pm2_env.pm_out_log_path}, {
  'pid path': pm2_env.pm_pid_path}, {
  'mode': pm2_env.exec_mode}, {
  'node v8 arguments': ''}, {
  'watch & reload': '\u2718'}, {
  'interpreter': pm2_env.exec_interpreter}, {
  'restarts': pm2_env.restart_time}, {
  'unstable restarts': pm2_env.unstable_restarts}, {
  'uptime': 0}, {
  'created at': created_at});
  }
  if ('pm_log_path' in pm2_env) {
    table.splice(6, 0, {
  'entire log path': pm2_env.pm_log_path});
  }
  console.log(table.toString());
  if (pm2_env.versioning) {
    var table2 = new Table({
  style: {
  'padding-left': 1, 
  head: ['cyan', 'bold'], 
  compact: true}});
    console.log('Revision control metadata');
    safe_push(table2, {
  'revision control': pm2_env.versioning.type}, {
  'remote url': pm2_env.versioning.url}, {
  'repository root': pm2_env.versioning.repo_path}, {
  'last update': pm2_env.versioning.update_time}, {
  'revision': pm2_env.versioning.revision}, {
  'comment': pm2_env.versioning.comment}, {
  'branch': pm2_env.versioning.branch});
    console.log(table2.toString());
  }
  if (pm2_env.axm_monitor && Object.keys(pm2_env.axm_monitor).length > 0) {
    var table_probes = new Table({
  style: {
  'padding-left': 1, 
  head: ['cyan', 'bold'], 
  compact: true}});
    console.log('Probes value');
    Object.keys(pm2_env.axm_monitor).forEach(function(key) {
  var obj = {};
  var value = pm2_env.axm_monitor[key].value || pm2_env.axm_monitor[key];
  obj[key] = value;
  safe_push(table_probes, obj);
});
    console.log(table_probes.toString());
  }
};
