function(channel) {
  that.connections.active_connection.gateway.part(channel);
}

function named() {
  that.classList.remove('clicked');
}