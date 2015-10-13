var p = require('path');
var fs = require('fs');
var util = require('util');
var debug = require('debug')('pm2:constants');
var PM2_ROOT_PATH = '';
if (process.env.PM2_HOME) 
  PM2_ROOT_PATH = process.env.PM2_HOME;
else 
  PM2_ROOT_PATH = p.resolve(process.env.HOME || process.env.HOMEPATH, '.pm2');
var csts = {
  PM2_CONF_FILE: p.join(PM2_ROOT_PATH, 'conf.js'), 
  CODE_UNCAUGHTEXCEPTION: 100, 
  CONCURRENT_ACTIONS: 1, 
  PREFIX_MSG: '\x1b[32m[PM2] \x1b[39m', 
  PREFIX_MSG_ERR: '\x1b[31m[PM2] [ERROR] \x1b[39m', 
  PREFIX_MSG_WARNING: '\x1b[33m[PM2] [WARN] \x1b[39m', 
  PREFIX_MSG_SUCCESS: '\x1b[36;1m[PM2] \x1b[39;0m', 
  SAMPLE_FILE_PATH: '../lib/samples/sample.json5', 
  SAMPLE_CONF_FILE: '../lib/samples/sample-conf.js', 
  CENTOS_STARTUP_SCRIPT: '../lib/scripts/pm2-init-centos.sh', 
  UBUNTU_STARTUP_SCRIPT: '../lib/scripts/pm2-init.sh', 
  SYSTEMD_STARTUP_SCRIPT: '../lib/scripts/pm2.service', 
  AMAZON_STARTUP_SCRIPT: '../lib/scripts/pm2-init-amazon.sh', 
  GENTOO_STARTUP_SCRIPT: '../lib/scripts/pm2', 
  SUCCESS_EXIT: 0, 
  ERROR_EXIT: 1, 
  ONLINE_STATUS: 'online', 
  STOPPED_STATUS: 'stopped', 
  STOPPING_STATUS: 'stopping', 
  LAUNCHING_STATUS: 'launching', 
  ERRORED_STATUS: 'errored', 
  ONE_LAUNCH_STATUS: 'one-launch-status', 
  CLUSTER_MODE_ID: 'cluster_mode', 
  FORK_MODE_ID: 'fork_mode', 
  KEYMETRICS_ROOT_URL: 'root.keymetrics.io', 
  REMOTE_PORT: 41624, 
  REMOTE_REVERSE_PORT: 43554, 
  REMOTE_HOST: 's1.keymetrics.io', 
  SEND_INTERVAL: 1000};
var default_conf = util._extend({
  PM2_ROOT_PATH: PM2_ROOT_PATH}, require('./lib/samples/sample-conf.js')(PM2_ROOT_PATH));
if (fs.existsSync(csts.PM2_CONF_FILE)) {
  try {
    var extra = require(csts.PM2_CONF_FILE)(PM2_ROOT_PATH);
    default_conf = util._extend(default_conf, extra);
  }  catch (e) {
  debug(e.stack || e);
}
}
var conf = util._extend(default_conf, csts);
if (process.platform === 'win32' || process.platform === 'win64') {
  debug('Windows detected');
  conf.DAEMON_RPC_PORT = '\\\\.\\pipe\\rpc.sock';
  conf.DAEMON_PUB_PORT = '\\\\.\\pipe\\pub.sock';
  conf.INTERACTOR_RPC_PORT = '\\\\.\\pipe\\interactor.sock';
}
module.exports = conf;
