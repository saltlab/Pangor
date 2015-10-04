function(channel) {
  this.connections.active_connection.gateway.part(channel);
}

function named() {
  this.classList.remove('clicked');
}