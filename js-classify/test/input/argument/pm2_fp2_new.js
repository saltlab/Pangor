CLI._startJson = function(cmd, opts, jsonVia, cb) {
  var appConf;
  var deployConf = null;
  var apps_info = [];

  if (typeof(cb) === 'undefined' && typeof(jsonVia) === 'function')
    cb = jsonVia;

  if (typeof(cmd) === 'object') {
    appConf = cmd;
  }
  else if (jsonVia == 'pipe') {
    appConf = json5.parse(cmd);
  }
  else {
    var data = null;
    try {
      data = fs.readFileSync(cmd);
    } catch(e) {
      printError(cst.PREFIX_MSG_ERR + 'JSON ' + cmd +' not found');
      return cb ? cb(e) : exitCli(cst.ERROR_EXIT);
    }
    appConf = parseConfig(data, cmd);
  }
};