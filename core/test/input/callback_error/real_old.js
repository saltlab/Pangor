/*
 * From WikiIRC. 296924
*/
function(err, server_num) {
  if (!err) {
    that.server_num = server_num;
    console.log("kiwi.gateway.socket.on('connect')");
  } else {
    console.log("kiwi.gateway.socket.on('error')", {
  reason: err});
  }
}