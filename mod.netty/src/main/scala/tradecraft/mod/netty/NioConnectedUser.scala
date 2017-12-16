package tradecraft.mod.netty

import io.netty.channel.socket.SocketChannel
import tradecraft.core.{ConnectedUser, Response}

class NioConnectedUser(socket: SocketChannel) extends ConnectedUser() {
  override def sendResponse(response: Response): Unit = {
    // output decoder knows how to write out response objects.
    socket.writeAndFlush(response)
  }
}
