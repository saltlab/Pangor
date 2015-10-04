function(options) {
  this._config = extend({}, options);
  this._isHTTPS = this._config.sslKey && this._config.sslCert ? true : false;
  var app = express();
  var httpServer;
  if (this._isHTTPS) {
    httpServer = https.createServer({
  key: fs.readFileSync(this._config.sslKey, {
  encoding: 'utf8'}), 
  cert: fs.readFileSync(this._config.sslCert, {
  encoding: 'utf8'})}, app);
  } else {
    httpServer = http.createServer(app);
  }
  this._httpServer = httpServer;
  app.use(favicon(path.join(__dirname, '../front-end-node/Images/favicon.png')));
  app.get('/', debugAction.bind(this));
  // BIND CALL IS GOING TO BE ADDED HERE:
  app.get('/debug', redirectToRoot);
  app.get('/inspector.json', inspectorJson);
  app.get('/json', jsonAction.bind(this));
  app.get('/json/version', jsonVersionAction.bind(this));
  app.use('/node', express.static(OVERRIDES));
  app.use(express.static(WEBROOT));
  this.wsServer = new WebSocketServer({
  server: httpServer});
  this.wsServer.on('connection', handleWebSocketConnection.bind(this));
  this.wsServer.on('error', handleServerError.bind(this));
  httpServer.on('listening', handleServerListening.bind(this));
  httpServer.on('error', handleServerError.bind(this));
  httpServer.listen(this._config.webPort, this._config.webHost);
}