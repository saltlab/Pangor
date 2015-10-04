function() {
    var self = this;

    this.listener.once('listening', function() {
        if (self.type === 'tcp') {
            var address = self.listener.address();
            self.info.address = address.address;
            self.info.port = address.port;
            self.info.uri = (self.settings.uri || (self.info.protocol + '://' + self.info.host + ':' + self.info.port));
        }
    });

    this._connections = {};
    
    this._onConnection = function(connection) {
        var key = connection.remoteAddress + ':' + connection.remotePort;
        self._connections[key] = connection;
        connection.once('close', function() {
            delete self._connections[key];
        });
    };

    this.listener.on('connection', this._onConnection);
}