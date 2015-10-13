function() {
  if ((!websocket.sentQUIT) && (websocket.ircSocket)) {
    websocket.ircSocket.end('QUIT :' + config.quit_message + '\r\n');
    websocket.sentQUIT = true;
    websocket.ircSocket.destroySoon();
  }
  con = connections[websocket.kiwi.address];
  con.count -= 1;
  con.sockets = _.reject(con.sockets, function(sock) {
  return sock === websocket;
});
}